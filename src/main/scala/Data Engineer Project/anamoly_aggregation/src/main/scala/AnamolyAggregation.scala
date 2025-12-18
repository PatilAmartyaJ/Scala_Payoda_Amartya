import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.text.SimpleDateFormat
import java.util.Calendar
import org.apache.spark.sql.protobuf.functions.from_protobuf

import java.sql.DriverManager

object AnamolyAggregation {

  def calculateAggregations(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val CASSANDRA_USERNAME = config.getString("cassandra.username")
    val CASSANDRA_PASSWORD = config.getString("cassandra.password")
    val CASSANDRA_CONSISTENCY = config.getString("cassandra.consistency")
    val TRUSTSTORE_PATH = config.getString("cassandra.truststore.path")
    val TRUSTSTORE_PASSWORD = config.getString("cassandra.truststore.password")
    val S3_BUCKET_NAME = config.getString("s3.bucket")
    val S3_ACCESS_KEY = config.getString("s3.access_key")
    val S3_SECRET_KEY = config.getString("s3.secret_key")

    val cassandraConfigs = Map(
      "spark.cassandra.connection.host" -> "cassandra.us-east-1.amazonaws.com",
      "spark.cassandra.connection.port" -> "9142",
      "spark.cassandra.connection.ssl.enabled" -> "true",
      "spark.cassandra.auth.username" -> CASSANDRA_USERNAME,
      "spark.cassandra.auth.password" -> CASSANDRA_PASSWORD,
      "spark.cassandra.input.consistency.level" -> CASSANDRA_CONSISTENCY,
      "spark.cassandra.connection.ssl.trustStore.path" -> TRUSTSTORE_PATH,
      "spark.cassandra.connection.ssl.trustStore.password" -> TRUSTSTORE_PASSWORD,
      "spark.sql.streaming.checkpointLocation" -> "/tmp/checkpoint/env-monitoring"
    )
    val s3Configs = Map(
      "spark.hadoop.fs.s3a.aws.credentials.provider" -> "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider",
      "spark.hadoop.fs.s3a.access.key" -> S3_ACCESS_KEY,
      "spark.hadoop.fs.s3a.secret.key" -> S3_SECRET_KEY,
      "spark.hadoop.fs.s3a.endpoint" -> "s3.amazonaws.com",
      "spark.hadoop.fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.S3AFileSystem"
    )

    val spark = {
      val builder = SparkSession.builder()
        .appName("Pipeline - aggregations")
        .master("local[*]")

      applyConfigs(builder, cassandraConfigs)
      applyConfigs(builder, s3Configs)
      builder.getOrCreate()
    }
    import spark.implicits._
    val t1=System.nanoTime()
    var executionDate= new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime)
    if (args.length > 1){
      executionDate=args(1)
    }
    // read particular s3 databucket based on date it has pb file and convert it to dataframe for further aggregations

    val descriptorResource = "/Environmental_Anomaly_Detection.desc"

    val stream = this.getClass.getResourceAsStream(descriptorResource)
    if (stream == null)
      throw new RuntimeException(s"Descriptor not found: $descriptorResource")

    val descriptorBytes =
      try Iterator.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray
      finally stream.close()

    val path = s"s3a://$S3_BUCKET_NAME//anomalies_detection/event_date=$executionDate/batch-$executionDate.pb"

    val binaryDF = spark.read
      .format("binaryFile")
      .option("recursiveFileLookup", "true")
      .load(path)
      .select($"content")          // drop metadata columns early
      .as[Array[Byte]]

    import java.nio.ByteBuffer
    import scala.collection.mutable.ArrayBuffer

    def extractMessages(bytes: Array[Byte]): Seq[Array[Byte]] = {
      val buf = ByteBuffer.wrap(bytes)
      val out = ArrayBuffer[Array[Byte]]()
      while (buf.remaining() > 4) {
        val length = buf.getInt()
        if (length <= 0 || length > buf.remaining()) {
          // corrupted or incomplete → stop gracefully
          return out.toSeq
        }
        val msg = new Array[Byte](length)
        buf.get(msg)
        out += msg
      }
      out.toSeq
    }

    // Expand each file into multiple protobuf messages
    val expanded = binaryDF
      .flatMap(bytes => extractMessages(bytes))
      .toDF("pb_bytes")

   // t = logStep("Read + expanded protobuf messages from S3", t)

    // Decode each message using spark-protobuf
    val anomaly_df_raw = expanded
      .select(
        from_protobuf(
          col("pb_bytes"),
          "environmental.Environmental_Anomaly_Detection",
          descriptorBytes
        ).alias("pb")
      )
      .select("pb.*")

    val anomaly_df_clean = anomaly_df_raw
      .withColumn("event_date", to_date(col("event_date"), "yyyy-MM-dd"))


    val mysqlProps = new java.util.Properties()
    mysqlProps.put("user", "admin")
    mysqlProps.put("password", "Amartya123")
    mysqlProps.put("driver", "com.mysql.cj.jdbc.Driver")

    val jdbcUrl = "jdbc:mysql://amartya-spark.cy9wucsyybxx.us-east-1.rds.amazonaws.com:3306/amartya_spark"
    Class.forName("com.mysql.cj.jdbc.Driver")
    val connection = DriverManager.getConnection(jdbcUrl, mysqlProps)

    val pollutionThresholdDF = spark.read
      .jdbc(jdbcUrl, "pollution_threshold", mysqlProps)

    val zoneDf= spark.read.jdbc(jdbcUrl,"zone",mysqlProps)


    val pollution_broadcast    = broadcast(pollutionThresholdDF)
    val zone_broadcast  = broadcast(zoneDf)


    val agg_df_join=anomaly_df_clean
      .join(zone_broadcast,Seq("zone_id"),"inner")
      .groupBy(col("zone_id"),col("name"),col("city"),col("latitude"),col("longitude"),col("event_date"))
      .agg(
        avg("pm2_5").as("avg_pm25"),
        avg("pm10").as("avg_pm10"),
        avg("co2_ppm").as("avg_co2"),
        sum(when(col("is_anomaly"), 1).otherwise(0)).as("anomaly_count")
      )



    agg_df_join.write
      .format("jdbc")
      .option("url", jdbcUrl)
      .option("dbtable", "daily_pollution_summary")
      .option("user", "admin")
      .option("password", "Amartya123")
      .mode("append")
      .save()


  }
    def applyConfigs(builder: SparkSession.Builder, configs: Map[String, String]): SparkSession.Builder = {
      configs.foreach { case (k, v) => builder.config(k, v) }
      builder
    }


}

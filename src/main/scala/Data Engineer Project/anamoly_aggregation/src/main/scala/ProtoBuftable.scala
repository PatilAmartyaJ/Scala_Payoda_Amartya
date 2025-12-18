import AnamolyAggregation.applyConfigs
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.to_protobuf
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.conf.Configuration


import java.sql.DriverManager

object ProtoBuftable {

  def dumpintos3(args:Array[String]): Unit = {

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
    val mysqlProps = new java.util.Properties()
    mysqlProps.put("user", "admin")
    mysqlProps.put("password", "Amartya123")
    mysqlProps.put("driver", "com.mysql.cj.jdbc.Driver")

    import spark.implicits._
    val descriptorResource = "/Daily_Avg_Pm_By_Zone.desc"




    val jdbcUrl = "jdbc:mysql://amartya-spark.cy9wucsyybxx.us-east-1.rds.amazonaws.com:3306/amartya_spark"
    Class.forName("com.mysql.cj.jdbc.Driver")
    val connection = DriverManager.getConnection(jdbcUrl, mysqlProps)

    val daily_pollution_hist = spark.read
      .jdbc(jdbcUrl, "daily_pollution_summary", mysqlProps)


    val daily_pollution_hist_converted = daily_pollution_hist.withColumn(
      "event_date",
      date_format(col("event_date"), "yyyy-MM-dd")
    )

    val daily_anomaly_count=daily_pollution_hist_converted
      .groupBy("event_date")
      .agg(
        sum(col("anomaly_count")).alias("total_anomaly_count")
      )
      .orderBy("event_date")

    /*

  string zone_id=1;
  string event_date=2;
  double avg_pm_25=3;
  double avg_pm10=4;
    */
    val daily_avg_pm_by_zone = daily_pollution_hist_converted
      .withColumn("proto_bytes",
        to_protobuf(
          struct($"zone_id",
            $"event_date",
            $"avg_pm25",
            $"avg_pm10",
          ),
          "environmental.Daily_Avg_Pm_By_Zone",
          generateDiscriptor("/Daily_Avg_Pm_By_Zone.desc")
        )
      )
      .select($"proto_bytes",$"zone_id",$"event_date")


    val daily_avg_co2_by_zone= daily_pollution_hist_converted
      .withColumn("proto_bytes",
        to_protobuf(
          struct($"zone_id",
            $"event_date",
            $"avg_co2"
          ),
          "environmental.Daily_Avg_Co2_By_Zone",
          generateDiscriptor("/Daily_Avg_Co2_By_Zone.desc")
        )
      )
      .select($"proto_bytes",$"zone_id",$"event_date")

    val daily_anomaly_counts= daily_anomaly_count
      .withColumn("proto_bytes",
        to_protobuf(
          struct($"event_date",
            $"total_anomaly_count",
          ),
          "environmental.Daily_Anomaly_Counts",
          generateDiscriptor("/Daily_Anomaly_Counts.desc")
        )
      )
      .select($"proto_bytes", $"event_date")

    if (!daily_avg_pm_by_zone.isEmpty) {
      // Solution: Partition by (event_date, zone_id) for better organization
      daily_avg_pm_by_zone.rdd
        .map(row =>
          ((row.getAs[String]("event_date"), row.getAs[String]("zone_id")),
            row.getAs[Array[Byte]]("proto_bytes"))
        )
        .groupByKey()
        .foreachPartition { partition: Iterator[((String, String), Iterable[Array[Byte]])] =>

          // Get Hadoop configuration from environment
          val conf = {
            val config = new Configuration()
            config.set("fs.s3a.access.key", S3_ACCESS_KEY)
            config.set("fs.s3a.secret.key", S3_SECRET_KEY)
            config.set("fs.s3a.endpoint", "s3.amazonaws.com")
            config
          }

          partition.foreach { case ((event_date, zone_id), protoBytesIter) =>
            // Create zone-specific file path
            val filePath = s"s3a://$S3_BUCKET_NAME/pm_summary/event_date=$event_date/zone=$zone_id.pb"

            val path = new Path(filePath)
            val fs = path.getFileSystem(conf)

            // Create directory structure if it doesn't exist
            val parentDir = path.getParent
            if (!fs.exists(parentDir)) {
              fs.mkdirs(parentDir)
            }

            val out = fs.create(path, true)

            try {
              protoBytesIter.foreach { bytes =>
                out.writeInt(bytes.length)
                out.write(bytes)
              }
              println(s"Wrote Protobuf file to $filePath with ${protoBytesIter.size} records for zone $zone_id")
            } finally {
              out.close()
            }
          }
        }
    }
    if (!daily_avg_co2_by_zone.isEmpty) {
      // Partition by both event_date and zone_id
      daily_avg_co2_by_zone.rdd
        .map(row =>
          ((row.getAs[String]("event_date"), row.getAs[String]("zone_id")),
            row.getAs[Array[Byte]]("proto_bytes"))
        )
        .groupByKey()
        .foreachPartition { partition: Iterator[((String, String), Iterable[Array[Byte]])] =>

          // Get Hadoop configuration from environment
          val conf = {
            val config = new Configuration()
            config.set("fs.s3a.access.key", S3_ACCESS_KEY)
            config.set("fs.s3a.secret.key", S3_SECRET_KEY)
            config.set("fs.s3a.endpoint", "s3.amazonaws.com")
            config
          }

          partition.foreach { case ((event_date, zone_id), protoBytesIter) =>
            // Create zone-specific file path for CO2 data
            val filePath = s"s3a://$S3_BUCKET_NAME/co2_summary/event_date=$event_date/zone=$zone_id.pb"

            val path = new Path(filePath)
            val fs = path.getFileSystem(conf)

            // Create directory structure if it doesn't exist
            val parentDir = path.getParent
            if (!fs.exists(parentDir)) {
              fs.mkdirs(parentDir)
            }

            val out = fs.create(path, true)

            try {
              protoBytesIter.foreach { bytes =>
                out.writeInt(bytes.length)
                out.write(bytes)
              }
              println(s"Wrote CO2 Protobuf file to $filePath with ${protoBytesIter.size} records for zone $zone_id")
            } finally {
              out.close()
            }
          }
        }
    }
    if (!daily_anomaly_counts.isEmpty) {

      // Solution 1: Use foreachPartition properly
      daily_anomaly_counts.select("event_date", "proto_bytes").rdd
        .map(row => (row.getAs[String]("event_date"), row.getAs[Array[Byte]]("proto_bytes")))
        .groupByKey()
        .foreachPartition { partition: Iterator[(String, Iterable[Array[Byte]])] =>

          // Get Hadoop configuration from environment
          val conf = {
            val config = new Configuration()
            config.set("fs.s3a.access.key", S3_ACCESS_KEY)
            config.set("fs.s3a.secret.key", S3_SECRET_KEY)
            config.set("fs.s3a.endpoint", "s3.amazonaws.com")
            config
          }

          partition.foreach { case (event_date, protoBytesIter) =>
            val filePath = s"s3a://$S3_BUCKET_NAME/anomaly_summary/event_date=$event_date/batch-$event_date.pb"

            val path = new Path(filePath)
            val fs = path.getFileSystem(conf)
            val out = fs.create(path, true)

            try {
              protoBytesIter.foreach { bytes =>
                out.writeInt(bytes.length)
                out.write(bytes)
              }
              println(s"Wrote Protobuf file to $filePath with ${protoBytesIter.size} records")
            } finally {
              out.close()
            }
          }
        }
    }


    spark.streams.awaitAnyTermination()
  }

  def generateDiscriptor(descriptorPath:String):Array[Byte]={
    val stream = this.getClass.getResourceAsStream(descriptorPath)
    if (stream == null)
      throw new RuntimeException(s"Descriptor not found: $descriptorPath")

    val descriptorBytes =
      try Iterator.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray

      finally stream.close()
    descriptorBytes
  }
}


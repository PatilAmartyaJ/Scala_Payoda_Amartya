
import org.apache.spark.sql._
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.cassandra.{DataFrameReaderWrapper, DataFrameWriterWrapper}
import org.apache.spark.sql.protobuf.functions.to_protobuf

import java.io.{BufferedOutputStream, OutputStream}

object StreamProcessor {

  def main(args: Array[String]): Unit = {
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
        .appName("Pipeline - Cassandra with s3")
        .master("local[*]")

      applyConfigs(builder, cassandraConfigs)
      applyConfigs(builder, s3Configs)

      builder.getOrCreate()
    }

    import spark.implicits._



    // ====================================================================
    // 3. ANOMALY DETECTION UDF
    // ====================================================================

    // UDF for anomaly detection using broadcast thresholds


    // Read from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "air_sensor_readings")
      .option("startingOffsets", "earliest")
      .load()

    val sensorSchema = StructType(Seq(
      StructField("sensorId", StringType),
      StructField("zoneId", StringType),
      StructField("pm25", DoubleType),
      StructField("pm10", DoubleType),
      StructField("co2Ppm", DoubleType),
      StructField("deviceStatus", StringType),
      StructField("event_time", LongType)
    ))

    // ====================================================================
    // 5. PROCESS STREAM
    // ====================================================================

    val processedDF = kafkaDF
      .select(from_json(col("value").cast(StringType), sensorSchema).as("data"))
      .select("data.*")
      .withColumn("processing_timestamp", lit(System.currentTimeMillis()).cast("long"))
      .withColumn("event_date",to_date((col("event_time") / 1000).cast("timestamp")).cast("string"))
      //      .withColumn("anomaly_info", detectAnomalyUDF(
      //        col("zoneId"),
      //        col("pm25"),
      //        col("pm10"),
      //        col("co2Ppm"),
      //        col("deviceStatus")
      //      ))
      .select(
        col("sensorId").as("sensor_id"),
        col("zoneId").as("zone_id"),
        col("pm25").as("pm2_5"),
        col("pm10"),
        col("co2Ppm").as("co2_ppm"),
        col("deviceStatus").as("device_status"),
        col("event_time"),
        col("event_date"),
        col("processing_timestamp"),
        //        col("anomaly_info._1").as("is_anomaly"),
        //        col("anomaly_info._2").as("anomaly_type"),
        //        col("anomaly_info._3").as("severity_score"),
        //        col("anomaly_info._4").as("exceeded_pollutants"),
        //        col("anomaly_info._5").as("exceed_percentages")
      )


    val descriptorResource = "/EnvironmentalEvent.desc"

    val stream = this.getClass.getResourceAsStream(descriptorResource)
    if (stream == null)
      throw new RuntimeException(s"Descriptor not found: $descriptorResource")

    val descriptorBytes =
      try Iterator.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray
      finally stream.close()


    //Write to Cassandra (Amazon Keyspace)
    val query = processedDF
      .writeStream
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        // Write to Cassandra - recent readings (keeping only latest 50 per zone)
        batchDF
          .select(
            col("sensor_id"),
            col("zone_id"),
            col("event_time"),
            col("pm2_5"),
            col("pm10"),
            col("co2_ppm"),
            col("device_status"),
            col("processing_timestamp")
            //            col("anomaly_info._1").as("is_anomaly"),
            //            col("anomaly_info._2").as("anomaly_type"),
            //            col("anomaly_info._3").as("severity_score"),
            //            col("anomaly_info._4").as("exceeded_pollutants"),
            //            col("anomaly_info._5").as("exceed_percentages")
          )
          .write
          .cassandraFormat("sensor_readings_by_zone", "environment_grid")
          .mode("append")
          .save()
        println("Writting in cassandra done")
        // Clean up old readings (keep only 50 per zone)
        //cleanupOldReadings(batchDF, spark)
      }
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .start()

    val protoEncoded = processedDF
      .withColumn("proto_bytes",
        to_protobuf(
          struct($"sensor_id", $"zone_id", $"event_time",$"event_date",$"pm2_5",
            $"pm10", $"co2_ppm", $"device_Status",
            $"processing_timestamp"),
          "environmental.EnvironmentalEvent",
          descriptorBytes
        )
      )
      .select($"proto_bytes", $"event_date")

    protoEncoded.writeStream
      .foreachBatch { (batch: DataFrame, batchId: Long) =>

        if (!batch.isEmpty) {

          batch.collect().groupBy(row => row.getAs[String]("event_date"))
            .foreach { case (event_date, rows) =>

              val filePath =
                s"s3a://${S3_BUCKET_NAME}/zones/event_date=$event_date/batch-$event_date.pb"

              val hadoopPath = new org.apache.hadoop.fs.Path(filePath)
              val fs = hadoopPath.getFileSystem(batch.sparkSession.sparkContext.hadoopConfiguration)
              val out = fs.create(hadoopPath, true)

              try {
                rows.foreach { row =>
                  val bytes = row.getAs[Array[Byte]]("proto_bytes")
                  out.writeInt(bytes.length)
                  out.write(bytes)
                }
                println(s"✔ Wrote Protobuf file → $filePath")
              } finally {
                out.close()
              }
            }
        }

      }
      .start()

    query.awaitTermination()
    spark.streams.awaitAnyTermination()

  }

  def cleanupOldReadings(df: DataFrame, spark: SparkSession): Unit = {
    // This is a simplified version - in production you'd use TTL or scheduled cleanup
    val zones = df.select("zoneId").distinct().collect()

    zones.foreach { row =>
      val zoneId = row.getInt(0)

      // Get current count for zone
      val countDF = spark.read
        .cassandraFormat("sensor_readings_by_zone", "environment_grid")
        .load()
        .filter(col("zone_id") === zoneId)

      val count = countDF.count()

      if (count > 50) {
        // Delete oldest readings beyond 50
        val toDelete = countDF
          .orderBy(col("event_time").desc)
          .limit(count.toInt - 50)
          .select("zone_id", "event_time", "sensor_id")

        // In Cassandra, you'd need to delete each row individually
        // This is simplified - actual implementation would use batch deletes
      }
    }
  }



  def writeVarInt(out: OutputStream, value: Int): Unit = {
    var v = value
    while ((v & 0xFFFFFF80) != 0L) {
      out.write((v & 0x7F) | 0x80)
      v >>>= 7
    }
    out.write(v & 0x7F)
  }

  def applyConfigs(builder: SparkSession.Builder, configs: Map[String, String]): SparkSession.Builder = {
    configs.foreach { case (k, v) => builder.config(k, v) }
    builder
  }

}

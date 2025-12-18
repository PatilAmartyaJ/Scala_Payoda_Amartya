import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.cassandra.DataFrameWriterWrapper
import org.apache.spark.sql.functions.{col, udf}

import java.sql.DriverManager
import java.util.Properties

object AnamolyDetection {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val CASSANDRA_USERNAME = config.getString("cassandra.username")
    val CASSANDRA_PASSWORD = config.getString("cassandra.password")
    val CASSANDRA_CONSISTENCY = config.getString("cassandra.consistency")
    val TRUSTSTORE_PATH = config.getString("cassandra.truststore.path")
    val TRUSTSTORE_PASSWORD = config.getString("cassandra.truststore.password")
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
      "fs.s3a.access.key" -> S3_ACCESS_KEY,
      "fs.s3a.secret.key" -> S3_SECRET_KEY,
      "fs.s3a.endpoint" -> "s3.amazonaws.com",
      "fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.S3AFileSystem"
    )

    val spark = {
      val builder = SparkSession.builder()
        .appName("Pipeline - Cassandra with s3")
        .master("local[*]")

      applyConfigs(builder, cassandraConfigs)
      applyConfigs(builder, s3Configs)
      builder.getOrCreate()
    }









    val mysqlProps = new java.util.Properties()
    mysqlProps.put("user", "admin")
    mysqlProps.put("password", "Amartya123")
    mysqlProps.put("driver", "com.mysql.cj.jdbc.Driver")


    val thresholdCache = loadThresholds(spark, mysqlProps)


    // Create broadcast variable once - it will be available to all executors
    val broadcastThresholds = spark.sparkContext.broadcast(thresholdCache)
    println("Thresholds broadcast to all executors")

    val detectAnomalyUDF = udf { (
                                   zoneId: String,
                                   pm25: Double,
                                   pm10: Double,
                                   co2Ppm: Double,
                                   deviceStatus: String
                                 ) =>
      val thresholds = broadcastThresholds.value

      thresholds.get(zoneId) match {
        case Some(threshold) =>
          // Check if any pollutant exceeds threshold
          val pm25Exceeds = pm25 > threshold.pm25Limit
          val pm10Exceeds = pm10 > threshold.pm10Limit
          val co2Exceeds = co2Ppm > threshold.co2Limit

          // Calculate which pollutants exceeded and by how much
          val exceededPollutants = Seq(
            if (pm25Exceeds) Some("PM2.5") else None,
            if (pm10Exceeds) Some("PM10") else None,
            if (co2Exceeds) Some("CO2") else None
          ).flatten.mkString(",")

          val exceedPercentages = Seq(
            if (pm25Exceeds) Some(f"${((pm25 - threshold.pm25Limit) / threshold.pm25Limit * 100).toInt}percent") else None,
            if (pm10Exceeds) Some(f"${((pm10 - threshold.pm10Limit) / threshold.pm10Limit * 100).toInt}percent") else None,
            if (co2Exceeds) Some(f"${((co2Ppm - threshold.co2Limit) / threshold.co2Limit * 100).toInt}percent") else None
          ).flatten.mkString(",")

          val isPollutantAnomaly = pm25Exceeds || pm10Exceeds || co2Exceeds
          val isDeviceAnomaly = deviceStatus != "OK"
          val isAnomaly = isPollutantAnomaly || isDeviceAnomaly

          if (isAnomaly) {
            // Determine anomaly type
            val anomalyType =
              if (isDeviceAnomaly && isPollutantAnomaly) "DEVICE_AND_POLLUTION"
              else if (isDeviceAnomaly) "DEVICE_ERROR"
              else "POLLUTION_EXCEEDED"

            // Calculate severity (0-3 scale)
            val severityScore =
              (if (pm25Exceeds) 1 else 0) +
                (if (pm10Exceeds) 1 else 0) +
                (if (co2Exceeds) 1 else 0) +
                (if (isDeviceAnomaly) 1 else 0)

            (true, anomalyType, severityScore, exceededPollutants, exceedPercentages)
          } else {
            (false, "NORMAL", 0, "", "")
          }

        case None =>
          // No threshold defined for this zone
          println(s"WARNING: No threshold defined for zone $zoneId")
          (false, "NO_THRESHOLD", 0, "", "")
      }
    }
    //      .withColumn("anomaly_info", detectAnomalyUDF(
    //        col("zoneId"),
    //        col("pm25"),
    //        col("pm10"),
    //        col("co2Ppm"),
    //        col("deviceStatus")
    //      ))

    val cass_df = spark.read
      .format("org.apache.spark.sql.cassandra")
      .options(Map(
        "keyspace" -> "environment_grid",
        "table" -> "sensor_readings_by_zone"
      ))
      .load()

    val anomalyDf=cass_df.withColumn("anomaly_info", detectAnomalyUDF(
            col("zone_id"),
            col("pm2_5"),
            col("pm10"),
            col("co2_ppm"),
            col("device_Status")
          ))
  .select(
    col("sensor_id"),
    col("zone_id"),
    col("event_time"),
    col("pm2_5"),
    col("pm10"),
    col("co2_ppm"),
    col("device_status"),
    col("processing_timestamp"),
    col("anomaly_info._1").as("is_anomaly"),
    col("anomaly_info._2").as("anomaly_type"),
    col("anomaly_info._3").as("severity_score"),
    col("anomaly_info._4").as("exceeded_pollutants"),
    col("anomaly_info._5").as("exceed_percentages"))
 anomalyDf.show(false)
 println( anomalyDf.count())
  anomalyDf.write
    .cassandraFormat("sensors_with_anomalies", "environment_grid")
    .mode("append")
    .save()
  }
  def loadThresholds(spark: SparkSession, mysqlProps: Properties): Map[String, PollutionThreshold] = {
    val jdbcUrl = "jdbc:mysql://amartya-spark.cy9wucsyybxx.us-east-1.rds.amazonaws.com:3306/amartya_spark"
    Class.forName("com.mysql.cj.jdbc.Driver")
    val connection = DriverManager.getConnection(jdbcUrl, mysqlProps)

    try {
      val pollutionThresholdDF = spark.read
        .jdbc(jdbcUrl, "pollution_threshold", mysqlProps)

      pollutionThresholdDF.collect().map { row =>
        val zoneId = row.getAs[String]("zone_id")
        val threshold = PollutionThreshold(
          zoneId = zoneId,
          pm25Limit = convertToDouble(row.getAs[java.math.BigDecimal]("pm2_5_limit")),
          pm10Limit = convertToDouble(row.getAs[java.math.BigDecimal]("pm10_limit")),
          co2Limit = convertToDouble(row.getAs[java.math.BigDecimal]("co2_limit")),
          alertLevel = row.getAs[String]("alert_level")
        )
        (zoneId, threshold)
      }.toMap

    }
    finally {
      connection.close()
    }
  }

  def convertToDouble(bd: java.math.BigDecimal): Double = {
    if (bd == null) 0.0 else bd.doubleValue()
  }
  def applyConfigs(builder: SparkSession.Builder, configs: Map[String, String]): SparkSession.Builder = {
    configs.foreach { case (k, v) => builder.config(k, v) }
    builder
  }


}

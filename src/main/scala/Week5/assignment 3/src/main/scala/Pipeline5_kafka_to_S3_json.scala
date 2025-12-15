import MainClass.applyConfigs
import org.apache.spark.sql.SparkSession
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.avro.from_avro
import org.apache.spark.sql.functions._
object Pipeline5_kafka_to_S3_json {

  def main(args:Array[String]): Unit = {
    // Define Avro schema
    val config = ConfigFactory.load()
    val S3_ACCESS_KEY=config.getString("s3.access_key")
    val S3_SECRET_KEY=config.getString("s3.secret_key")

    val s3Configs = Map(
      "spark.hadoop.fs.s3a.aws.credentials.provider"-> "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider",
      "fs.s3a.access.key" -> S3_ACCESS_KEY,
      "fs.s3a.secret.key" -> S3_SECRET_KEY,
      "fs.s3a.endpoint" -> "s3.amazonaws.com",
      "fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.S3AFileSystem"
    )
    val spark={
      val builder = SparkSession.builder()
        .appName("Pipeline - s3")
        .master("local[*]")
      applyConfigs(builder, s3Configs)
      builder.getOrCreate()
    }

    import spark.implicits._

    val KAFKA_BOOTSTRAP_SERVER = config.getString("kafka.bootstrapServer")
    val KAFKA_TOPIC = config.getString("kafka.topic")
    val s3_BUCKET_NAME=config.getString("s3.bucket")
    // Console output for testi


    val schemaPath = "src/main/resources/orders.avsc"
    val avroSchema = spark.read.textFile(schemaPath).collect().mkString(" ")

    // --------------------------------------------------------------------
    // 1. Read Kafka Stream
    // --------------------------------------------------------------------
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVER)
      .option("subscribe", KAFKA_TOPIC)
      .option("startingOffsets", "latest")
      .load()

    // --------------------------------------------------------------------
    // 2. Decode Avro data
    // --------------------------------------------------------------------
    val decodedDF = kafkaDF.select(
      from_avro(col("value"), avroSchema).as("data")
    ).select("data.*")

    // --------------------------------------------------------------------
    // 3. Convert to JSON
    // --------------------------------------------------------------------
    val jsonDF = decodedDF.select(
      to_json(struct("*")).alias("value")
    )

    // --------------------------------------------------------------------
    // 4. Write JSON to S3 (Streaming Sink)
    // --------------------------------------------------------------------
    val query = jsonDF.writeStream
      .format("json")
      .option("path", s"s3a://${s3_BUCKET_NAME}/stream/json/")
      .option("checkpointLocation", s"s3a://${s3_BUCKET_NAME}/checkpoints/pipeline5/")
      .outputMode("append")
      .start()

    query.awaitTermination()

  }
  def applyConfigs(builder: SparkSession.Builder, configs: Map[String, String]): SparkSession.Builder = {
    configs.foreach { case (k, v) => builder.config(k, v) }
    builder
  }




}

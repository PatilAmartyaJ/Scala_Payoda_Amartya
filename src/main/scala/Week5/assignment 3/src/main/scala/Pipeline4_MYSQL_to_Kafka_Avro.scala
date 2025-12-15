import org.apache.spark.sql.{Dataset, Row, SparkSession}
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.avro.to_avro
import org.apache.spark.sql.functions._
object Pipeline4_MYSQL_to_Kafka_Avro {

  def mysql_to_kafka_avro(args:Array[String],spark:SparkSession): Unit = {
    import spark.implicits._


    val config = ConfigFactory.load()
    val MYSQL_HOST_URL = config.getString("mysql.host_url")
    val MYSQL_USERNAME = config.getString("mysql.username")
    val MYSQL_PASSWORD = config.getString("mysql.password")
    val KAFKA_BOOTSTRAP_SERVER = config.getString("kafka.bootstrapServer")
    val KAFKA_TOPIC = config.getString("kafka.topic")

    // Track last processed ID
    var lastMaxOrderId = 0

    // Dummy stream used only as a timer (5 sec trigger)
    val heartbeatStream = spark.readStream
      .format("rate")
      .option("rowsPerSecond", 5)
      .load()

    // -------------------------------------------
    // 3. Main Streaming Logic using foreachBatch
    // -------------------------------------------
    val query = heartbeatStream.writeStream
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>

        println("\n⏳ Checking MySQL for new rows...")

        // Load entire table (small table assumed)
        val mysqlDF = spark.read
          .format("jdbc")
          .option("url", MYSQL_HOST_URL)
          .option("dbtable", "new_orders")
          .option("user", MYSQL_USERNAME)
          .option("password", MYSQL_PASSWORD)
          .load()

        // New rows only
        val newRows = mysqlDF.filter(col("order_id") > lastMaxOrderId)

        if (newRows.count() > 0) {

          println(s"🚀 Found ${newRows.count()} new rows:")
          newRows.show(false)

          // Update max order_id
          lastMaxOrderId = newRows.agg(max("order_id")).first().getInt(0)

          // Encode using Avro schema
          val avroDF = newRows.select(
            to_avro(struct($"order_id", $"customer_id", $"amount", $"created_at"))
              .as("value")
          )

          // -------------------------------------------
          // 4. Write to Kafka as Avro messages
          // -------------------------------------------
          avroDF.write
            .format("kafka")
            .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVER)
            .option("topic", KAFKA_TOPIC)
            .save()

          println(s"📨 Successfully sent ${newRows.count()} Avro messages to Kafka!")
        }
        else {
          println("✔ No new rows found.")
        }

      }
      .trigger(org.apache.spark.sql.streaming.Trigger.ProcessingTime("5 seconds"))
      .start()

    query.awaitTermination()
  }
}


import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object Pipeline2_keyspace_to_parquet {

 def keyspace_to_parquet(args:Array[String],spark:SparkSession): Unit={
   val config = ConfigFactory.load()
   val s3_BUCKET_NAME=config.getString("s3.bucket")
   // Read from Amazon Keyspaces
   val salesDF = spark.read
     .format("org.apache.spark.sql.cassandra")
     .option("keyspace", "retail")
     .option("table", "sales_data")
     .load()

   // Select required columns
   val selectedDF = salesDF.select(
     "customer_id", "order_id", "amount", "product_name", "quantity"
   )
   selectedDF.show(10,false)

   // Write as partitioned Parquet to S3
   selectedDF
     .write
     .mode("overwrite")
     .partitionBy("customer_id")
     .parquet(s"s3a://${s3_BUCKET_NAME}/retail-output/sales/parquet/")

   println("Pipeline 2 completed successfully!")
   spark.stop()

 }
}

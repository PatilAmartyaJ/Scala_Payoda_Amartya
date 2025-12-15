import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
object Pipeline3_Parquet_to_json {

  def parquet_to_json(args:Array[String],spark:SparkSession): Unit = {
    val config = ConfigFactory.load()
    val s3_BUCKET_NAME=config.getString("s3.bucket")
    import spark.implicits._

    // Read partitioned parquet from S3
    val parquetDF = spark.read
      .parquet(s"s3a://${s3_BUCKET_NAME}/retail-output/sales/parquet/")

    // Aggregate by product
    val aggregatedDF = parquetDF
      .groupBy("product_name")
      .agg(
        sum("quantity").as("total_quantity"),
        sum("amount").as("total_revenue")
      )
      .orderBy($"total_revenue".desc)

    // Write as JSON to S3
    aggregatedDF.write
      .mode("overwrite")
      .json(s"s3a://${s3_BUCKET_NAME}/retail-output/aggregates/products.json")

    println("Pipeline 3 completed successfully!")

    // Show sample output
    aggregatedDF.show(10, truncate = false)
    spark.stop()
  }

}

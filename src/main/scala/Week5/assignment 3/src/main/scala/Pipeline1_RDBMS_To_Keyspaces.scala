import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object Pipeline1_RDBMS_To_Keyspaces {

   def writetoCassandra(args:Array[String],spark:SparkSession):Unit={
     import spark.implicits._

     // MySQL connection properties
     val config = ConfigFactory.load()
     val MYSQL_USERNAME=config.getConfig("mysql.username")
     val MYSQL_PASSWORD=config.getConfig("mysql.password")

     val mysqlProps = new java.util.Properties()
     mysqlProps.put("user",MYSQL_USERNAME )
     mysqlProps.put("password", MYSQL_PASSWORD)
     mysqlProps.put("driver", "com.mysql.cj.jdbc.Driver")

     val jdbcUrl = "jdbc:mysql://amartya-spark.cy9wucsyybxx.us-east-1.rds.amazonaws.com:3306/amartya_spark"

     // Read MySQL tables
     val customersDF = spark.read
       .jdbc(jdbcUrl, "customers", mysqlProps)

     val ordersDF = spark.read
       .jdbc(jdbcUrl, "orders", mysqlProps)

     val orderItemsDF = spark.read
       .jdbc(jdbcUrl, "order_items", mysqlProps)

     // Perform joins
     val joinedDF = customersDF
       .join(ordersDF, customersDF("customer_id") === ordersDF("customer_id"))
       .join(orderItemsDF, ordersDF("order_id") === orderItemsDF("order_id"))
       .select(
         customersDF("customer_id"),
         customersDF("name"),
         customersDF("email"),
         customersDF("city"),
         ordersDF("order_id"),
         ordersDF("order_date").cast("timestamp").as("order_date"),
         ordersDF("amount"),
         orderItemsDF("item_id"),
         orderItemsDF("product_name"),
         orderItemsDF("quantity")
       )

     // Write to Amazon Keyspaces
     joinedDF.write
       .format("org.apache.spark.sql.cassandra")
       .option("keyspace", "retail")
       .option("table", "sales_data")
       .mode("append")
       .save()

     println("Pipeline 1 completed successfully!")
     spark.stop()
   }
}

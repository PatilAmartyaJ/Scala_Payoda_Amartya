import org.apache.spark.sql.SparkSession
import com.typesafe.config.ConfigFactory

object MainClass {

  def main(args: Array[String]): Unit={

    val config = ConfigFactory.load()
    val CASSANDRA_USERNAME = config.getString("cassandra.username")
    val CASSANDRA_PASSWORD = config.getString("cassandra.password")
    val CASSANDRA_CONSISTENCY= config.getString("cassandra.consistency")
    val TRUSTSTORE_PATH= config.getString("cassandra.truststore.path")
    val TRUSTSTORE_PASSWORD = config.getString("cassandra.truststore.password")
    val S3_ACCESS_KEY=config.getString("s3.access_key")
    val S3_SECRET_KEY=config.getString("s3.secret_key")

    val cassandraConfigs = Map(
      "spark.cassandra.connection.host" -> "cassandra.us-east-1.amazonaws.com",
      "spark.cassandra.connection.port" -> "9142",
      "spark.cassandra.connection.ssl.enabled" -> "true",
      "spark.cassandra.auth.username" -> CASSANDRA_USERNAME,
      "spark.cassandra.auth.password" -> CASSANDRA_PASSWORD,
      "spark.cassandra.input.consistency.level" -> CASSANDRA_CONSISTENCY,
      "spark.cassandra.connection.ssl.trustStore.path" -> TRUSTSTORE_PATH,
      "spark.cassandra.connection.ssl.trustStore.password" -> TRUSTSTORE_PASSWORD
    )
    val s3Configs = Map(
      "spark.hadoop.fs.s3a.aws.credentials.provider"-> "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider",
      "fs.s3a.access.key" -> S3_ACCESS_KEY,
      "fs.s3a.secret.key" -> S3_SECRET_KEY,
      "fs.s3a.endpoint" -> "s3.amazonaws.com",
      "fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.S3AFileSystem"
    )


    val spark_with_Cassandra = {
      val builder = SparkSession.builder()
        .appName("Pipeline - Cassandra Only")
        .master("local[*]")

      applyConfigs(builder, cassandraConfigs)

      builder.getOrCreate()
    }


    val spark_with_Cassandra_s3 = {
      val builder = SparkSession.builder()
        .appName("Pipeline - Cassandra with s3")
        .master("local[*]")

      applyConfigs(builder, cassandraConfigs)
      applyConfigs(builder, s3Configs)
      builder.getOrCreate()
    }

    val spark_with_S3={
      val builder = SparkSession.builder()
        .appName("Pipeline - s3")
        .master("local[*]")
      applyConfigs(builder, s3Configs)
      builder.getOrCreate()
    }

    val spark_with_nothing={
      val builder = SparkSession.builder()
        .appName("spark with zero")
        .master("local[*]")
      builder.getOrCreate()
    }


    val args=Array("Pipeline4")
    if(args(0).equals("Pipeline1")){
       Pipeline1_RDBMS_To_Keyspaces.writetoCassandra(args,spark_with_Cassandra)
     }
    if(args(0).equals("Pipeline2")){
      Pipeline2_keyspace_to_parquet.keyspace_to_parquet(args,spark_with_Cassandra_s3)
    }
    if(args(0).equals("Pipeline3")){
      Pipeline3_Parquet_to_json.parquet_to_json(args,spark_with_S3)
    }
    if(args(0).equals("Pipeline4")){
      Pipeline4_MYSQL_to_Kafka_Avro.mysql_to_kafka_avro(args,spark_with_nothing)
      //Pipeline5_kafka_to_S3_json.kafka_to_S3_json(args,spark_with_S3)
    }

  }
  def applyConfigs(builder: SparkSession.Builder, configs: Map[String, String]): SparkSession.Builder = {
    configs.foreach { case (k, v) => builder.config(k, v) }
    builder
  }

}

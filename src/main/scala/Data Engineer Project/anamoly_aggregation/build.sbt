import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.13"

lazy val sparkVersion = "3.5.1"
lazy val root = (project in file("."))
  .settings(
    name := "anamoly_aggregation",
      libraryDependencies ++= Seq(
      // Spark Core
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,

      // Spark Streaming
      "org.apache.spark" %% "spark-streaming" % sparkVersion,
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
      "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,

      "org.apache.spark" %% "spark-protobuf" % sparkVersion,



      // Cassandra
      "com.datastax.spark" %% "spark-cassandra-connector" % "3.4.1",

      // MySQL JDBC
      "mysql" % "mysql-connector-java" % "8.0.33",

      // Kafka Client
      "org.apache.kafka" % "kafka-clients" % "3.5.1",

      // AWS (S3 support)
      "org.apache.hadoop" % "hadoop-common" % "3.3.1",
      "org.apache.hadoop" % "hadoop-aws" % "3.3.1",
      "org.apache.hadoop" % "hadoop-hdfs" % "3.3.1",
      "com.amazonaws" % "aws-java-sdk-bundle" % "1.11.901",
      "org.apache.hadoop" % "hadoop-hdfs" % "3.2.0",
      "com.typesafe" % "config" % "1.4.2",
      "com.github.jnr" % "jnr-posix" % "3.1.7",
      "joda-time" % "joda-time" % "2.10.10",
      //"com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.13" % "protobuf"

    )
  )

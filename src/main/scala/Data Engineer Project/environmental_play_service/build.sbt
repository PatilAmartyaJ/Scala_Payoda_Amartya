import sbt.Compile

import scala.collection.Seq

name := """environmental_play_service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick"            % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
  "mysql" % "mysql-connector-java" % "8.0.26",
  "org.apache.kafka" % "kafka-clients" % "3.5.1",
  "com.datastax.oss" % "java-driver-core" % "4.17.0",
  "com.datastax.oss" % "java-driver-mapper-runtime" % "4.17.0",

  // Amazon SigV4 Auth Plugin (critical for Amazon Keyspaces)
  "software.amazon.awssdk" % "auth" % "2.20.0",
  "software.amazon.awssdk" % "s3" % "2.20.0",
  "software.amazon.awssdk" % "sts" % "2.20.0"
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.20", // Akka Streams
  "com.typesafe.akka" %% "akka-actor" % "2.6.20",  // Akka Actor
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.20",
  "org.apache.pekko" %% "pekko-stream" % "1.0.1",
  "com.auth0" % "java-jwt" % "4.3.0", // Java JWT library
  "com.typesafe.play" %% "play-json" % "2.9.4", // Play JSON for JSON processing
  "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.13" % "protobuf",

)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value
)

libraryDependencies += filters
// In your build.sbt

// OR just ScalaPB without gRPC:

// Add dependencies


// Configure ScalaPB


libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
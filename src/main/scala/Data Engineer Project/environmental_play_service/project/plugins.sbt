addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.1")
// Play Framework plugin

// ScalaPB code generator plugin (REQUIRED)
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.7")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.6"
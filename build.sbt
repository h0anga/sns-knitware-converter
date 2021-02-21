name := "sns-knitware-converter"

version := "0.1.4"

scalaVersion := "2.13.1"

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.7.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.5" % "test"
libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % "2.1.0" % Test
libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.7.0-M8"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2"

libraryDependencies ++= Seq(
  "io.zipkin.brave" % "brave-instrumentation-kafka-clients" % "5.6.3",
  "io.zipkin.brave" % "brave-instrumentation-kafka-streams" % "5.6.3",
  "io.zipkin.reporter2" % "zipkin-sender-kafka11" % "2.8.1"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)
dockerBaseImage := "openjdk:8-jre-alpine"

mainClass in Compile := Some("sns.lando.knitware.converter.KnitwareConverterApp")
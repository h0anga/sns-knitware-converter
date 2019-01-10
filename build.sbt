name := "sns-knitware-converter"

version := "0.1.4"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % "2.1.0" % Test

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.5.0"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
//dockerBaseImage := "openjdk:jre-alpine" // a smaller JVM base image

mainClass in Compile := Some("sns.lando.knitware.converter.KnitwareConverterApp")
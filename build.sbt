name := "sns-knitware-converter"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.0.0"

lazy val testDependencies = Seq(
  "org.scalatest"              %% "scalatest"      % "3.0.5" % "test",
  "com.madewithtea"            %% "mockedstreams"  % "1.6.0" % "test"
)
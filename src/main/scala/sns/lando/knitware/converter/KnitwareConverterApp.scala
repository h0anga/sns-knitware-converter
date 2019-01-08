package sns.lando.knitware.converter

import scala.util.Properties

object KnitwareConverterApp extends App {

  val kafkabroker: String = Properties.envOrElse("KAFKA_BROKER_SERVER", "localhost")
  val kafkabrokerPort: String = Properties.envOrElse("KAFKA_BROKER_PORT", "9092")
  val kafkaSetup = new KafkaSetup(kafkabroker, kafkabrokerPort)
  kafkaSetup.setUp()

  sys.ShutdownHookThread {
    kafkaSetup.tearDown()
  }

}

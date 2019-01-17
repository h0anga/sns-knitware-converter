package sns.lando.knitware.converter

import scala.util.Properties

object KnitwareConverterApp extends App {

  private val kafkabroker: String = Properties.envOrElse("KAFKA_BROKER_SERVER", "localhost")
  private val kafkabrokerPort: String = Properties.envOrElse("KAFKA_BROKER_PORT", "9092")
  private val enrichedModifyTopic = "enriched.modification.instructions"
  private val switchModificationTopic = "switch.modification.instructions"

  val kafkaSetup = new KafkaSetup(kafkabroker, kafkabrokerPort)
  kafkaSetup.start(enrichedModifyTopic, switchModificationTopic)

  sys.ShutdownHookThread {
    kafkaSetup.shutDown()
  }
}

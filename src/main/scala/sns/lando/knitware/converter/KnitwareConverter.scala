package sns.lando.knitware.converter

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object KnitwareConverter extends App {


  private val lluStreamMessagesTopic = "incoming.op.msgs"
  private val switchModificationTopic = "switch.modification.instructions"
  private val KafkaDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
  private val KafkaSerializer = "org.apache.kafka.common.serialization.StringSerializer"

  private val props = new Properties()

  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.103.3.240:9092")
  props.put("acks", "all")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
  props.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass.getName)

  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sns-knitware-converter")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.103.3.240:9092")


  val builder: StreamsBuilder = new StreamsBuilder
  val textLines: KStream[String, String] = builder.stream[String, String](lluStreamMessagesTopic)

  def getXmlFor(textLine: String): String =
    """
      |<?xml version="1.0" encoding="UTF-8"?>
      |<switchServiceModificationInstruction switchServiceId="16" netstreamCorrelationId="33269793">
      |  <features>
      |    <callerDisplay active="true"/>
      |    <ringBack active="true"/>
      |    <chooseToRefuse active="true"/>
      |  </features>
      |</switchServiceModificationInstruction>
    """.stripMargin


  textLines.mapValues(textLine => getXmlFor(textLine))
    .to(switchModificationTopic)
  //  val wordCounts: KTable[String, Long] = textLines
  //    .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
  //    .groupBy((_, word) => word)
  //    .count(Materialized.as("counts-store"))
  //  wordCounts.toStream.to("WordsWithCountsTopic")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

}

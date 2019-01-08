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

class KafkaSetup(private val server: String, private val port: String) {
  private val lluStreamMessagesTopic = "incoming.op.msgs"
  private val switchModificationTopic = "switch.modification.instructions"
  private val KafkaDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
  private val KafkaSerializer = "org.apache.kafka.common.serialization.StringSerializer"

  private val props = new Properties()

  var streams: KafkaStreams = _

  def setUp(): Unit = {
    val bootstrapServers = server + port
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass.getName)

    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sns-knitware-converter")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, server + ":" + port)


    val builder: StreamsBuilder = new StreamsBuilder
    val textLines: KStream[String, String] = builder.stream[String, String](lluStreamMessagesTopic)


    textLines.mapValues(textLine => new KnitwareConverter().getXmlFor(textLine))
      .to(switchModificationTopic)

    streams = new KafkaStreams(builder.build(), props)
    streams.start()
    println("Completed setUp")
  }

  def tearDown(): Unit = {
    println("In tearDown")
    streams.close(10, TimeUnit.SECONDS)
  }
}

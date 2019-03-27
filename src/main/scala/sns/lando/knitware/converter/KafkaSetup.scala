package sns.lando.knitware.converter

import java.util.Properties

import brave.Tracing
import brave.kafka.streams.KafkaStreamsTracing
import brave.sampler.Sampler
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.scala.kstream.Consumed
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.kafka11.KafkaSender

class KafkaSetup(private val server: String, private val port: String) {
  private implicit val stringSerde: Serde[String] = Serdes.String()

  private var stream: KafkaStreams = _
  private val bootstrapServers = server + ":" + port
  private val tracing = setupTracing

  def setupTracing: KafkaStreamsTracing = {
    val sender = KafkaSender.newBuilder.bootstrapServers(bootstrapServers).build
    val reporter = AsyncReporter.builder(sender).build
    val tracing = Tracing.newBuilder.localServiceName("knitware-converter").sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build
    KafkaStreamsTracing.create(tracing)
  }

  def start(inputTopicName: String, outputTopicName: String) = {


    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "sns-knitware-converter")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
      settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings
    }
    val topology = build(inputTopicName, outputTopicName)
    stream = tracing.kafkaStreams(topology, streamingConfig)
    stream.start()
  }

  def shutDown(): Unit = {
    stream.close()
  }

  def build(inputTopicName: String, outputTopicName: String): Topology = {
    val builder = new StreamsBuilder

    builder.stream(inputTopicName, Consumed.`with`(stringSerde, stringSerde))
      .mapValues(line => new KnitwareConverter().getXmlFor(line))
      .to(outputTopicName)

    builder.build()
  }
}
package sns.lando.knitware.converter

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.streams.Consumed
import org.apache.kafka.streams.scala.kstream.Consumed

//import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStream

class KafkaSetup(private val server: String, private val port: String) {
  private val lluStreamMessagesTopic = "incoming.op.msgs"
  private val switchModificationTopic = "switch.modification.instructions"

  private val KafkaDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
  private val KafkaSerializer = "org.apache.kafka.common.serialization.StringSerializer"

  private implicit val stringSerde: Serde[String] = Serdes.String()

  //  val configuration: Config = ConfigFactory.load()

  private var stream: KafkaStreams = _

  def setUp(): Unit = {

    val bootstrapServers = server + ":" + port
    val builder = new StreamsBuilder

    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "sns-knitware-converter")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
      settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
      settings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
      settings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)

      settings
    }

    createConverterTopology(builder, lluStreamMessagesTopic, switchModificationTopic)

    stream = new KafkaStreams(builder.build(), streamingConfig)

    sys.addShutdownHook({
      stream.close()
    })

    stream.start()
  }

  def createConverterTopology(builder: StreamsBuilder, input: String, output: String): Unit = {
    // Read the input Kafka topic into a KStream instance.
    val textLines: KStream[String, String] = builder.stream(input)
    //    val uppercasedWithMapValues: KStream[String, String] = textLines.mapValues(new String(_).toUpperCase().getBytes())
    //    uppercasedWithMapValues.to(output)
    textLines.mapValues(textLine => new KnitwareConverter().getXmlFor(textLine))
      .to(output)
  }

  def tearDown(): Unit = {
    stream.close()
  }


  def build(inputTopicName: String, outputTopicName: String): Topology = {

    val bootstrapServers = server + ":" + port
    val builder = new StreamsBuilder

    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "sns-knitware-converter")
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
      settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSerializer)
      settings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
      settings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaDeserializer)
      settings
    }

    builder.stream(inputTopicName, Consumed.`with`(stringSerde, stringSerde))
      .mapValues(line => new KnitwareConverter().getXmlFor(line))
      .to(outputTopicName)

    return builder.build(streamingConfig)
  }

}
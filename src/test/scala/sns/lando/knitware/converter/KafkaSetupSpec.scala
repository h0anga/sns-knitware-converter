package sns.lando.knitware.converter

import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.{StreamsConfig, TestInputTopic, TestOutputTopic, TopologyTestDriver}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.util.{Properties, UUID}

class KafkaSetupSpec extends AnyFlatSpec with Matchers {
  private val kafkaApplicationId = "sns-knitware-converter"
  private val serverName = "serverName"
  private val portNumber = "portNumber"

  private val inputTopic = "topic-in"
  private val outputTopic = "topic-out"

  private val kafkaMessageInKey = "key"
  private val streamingConfig = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaApplicationId)
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, serverName + ":" + portNumber)
    settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    settings
  }

  private val orderId = UUID.randomUUID().toString
  private val serviceId = UUID.randomUUID().toString

  private val kafkaMessageInValue =
    s"""
       |{"modifyVoiceFeaturesInstruction":{"operatorId":"sky","orderId":"$orderId","serviceId":"$serviceId","operatorOrderId":"SogeaVoipModify_YHUORO","features":["CallerDisplay","RingBack","ChooseToRefuse"]}}
    """.stripMargin


  private val expectedOutput =
    s"""|<?xml version="1.0" encoding="UTF-8"?>
      |<switchServiceModificationInstruction switchServiceId="$serviceId" netstreamCorrelationId="$orderId">
      |  <features>
      |    <callerDisplay active="true"/>
      |    <ringBack active="true"/>
      |    <chooseToRefuse active="true"/>
      |  </features>
      |</switchServiceModificationInstruction>
    """.stripMargin

  private def createTopologyToTest = {
    val kafkaSetup = new KafkaSetup(serverName, portNumber)
    val topology = kafkaSetup.build(inputTopic, outputTopic)
    topology
  }

  it should "test a stream" in {
    val topology = createTopologyToTest
    val topologyTestDriver = new TopologyTestDriver(topology, streamingConfig)

    val keySerde: Serde[String] = Serdes.String
    val valueSerde: Serde[String] = Serdes.String

    val testServiceTopic: TestInputTopic[String, String] = topologyTestDriver.createInputTopic(inputTopic, keySerde.serializer(), valueSerde.serializer())
    testServiceTopic.pipeInput(kafkaMessageInKey, kafkaMessageInValue)

    val testOutputTopic: TestOutputTopic[String, String] = topologyTestDriver.createOutputTopic(outputTopic, keySerde.deserializer(), valueSerde.deserializer())
    val outputValue = testOutputTopic.readValue()

    outputValue shouldEqual expectedOutput
  }
}

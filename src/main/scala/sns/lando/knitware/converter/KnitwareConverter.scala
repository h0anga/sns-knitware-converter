package sns.lando.knitware.converter

import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import org.json4s.{Formats, NoTypeHints}

class KnitwareConverter {

  def getXmlFor(textLine: String): String = {
    println(s"input: ${textLine}")
    implicit val formats: Formats = Serialization.formats (NoTypeHints)
    val voiceFeatures = read[VoiceFeatures] (textLine)
    println(s"vf: ${voiceFeatures.netstreamCorrelationId}")

    return s"""
      |<?xml version="1.0" encoding="UTF-8"?>
      |<switchServiceModificationInstruction switchServiceId="16" netstreamCorrelationId="${voiceFeatures.netstreamCorrelationId}">
      |  <features>
      |    <callerDisplay active="true"/>
      |    <ringBack active="true"/>
      |    <chooseToRefuse active="true"/>
      |  </features>
      |</switchServiceModificationInstruction>
    """.stripMargin
  }
}

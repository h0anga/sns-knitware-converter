package sns.lando.knitware.converter

import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import org.json4s.{Formats, NoTypeHints}

class KnitwareConverter {

  def getXmlFor(textLine: String): String = {
    println(s"input: $textLine")
    implicit val formats: Formats = Serialization.formats (NoTypeHints)
    val voiceFeatures = read[VoiceFeatures] (textLine)

    s"""|<?xml version="1.0" encoding="UTF-8"?>
      |<switchServiceModificationInstruction switchServiceId="16" netstreamCorrelationId="${voiceFeatures.modifyVoiceFeaturesInstruction.orderId}">
      |  <features>
      |${featuresToJson(voiceFeatures.modifyVoiceFeaturesInstruction.features)}
      |  </features>
      |</switchServiceModificationInstruction>
    """.stripMargin
  }

  def featuresToJson(features: Seq[String]): String = {
    features.map(f => s"""    <${f.head.toString.toLowerCase + f.tail} active="true"/>""").mkString("\n")
  }
}

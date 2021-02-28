package sns.lando.knitware.converter

import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import org.json4s.{Formats, ShortTypeHints}

class KnitwareConverter {

  def getXmlFor(textLine: String): String = {
    println(s"input: $textLine")
    implicit val formats: Formats = Serialization.formats(ShortTypeHints(List(classOf[String])))
    val enrichedInstruction = read[EnrichedInstruction] (textLine)

    s"""|<?xml version="1.0" encoding="UTF-8"?>
      |<switchServiceModificationInstruction switchServiceId="${enrichedInstruction.modifyVoiceFeaturesInstruction.serviceId}" netstreamCorrelationId="${enrichedInstruction.modifyVoiceFeaturesInstruction.orderId}">
      |  <features>
      |${featuresToXml(enrichedInstruction.modifyVoiceFeaturesInstruction.features)}
      |  </features>
      |</switchServiceModificationInstruction>
    """.stripMargin
  }

  def featuresToXml(features: List[String]): String = {
    features.map(f => s"""    <${f.head.toString.toLowerCase + f.tail} active="true"/>""").mkString("\n")
  }
}

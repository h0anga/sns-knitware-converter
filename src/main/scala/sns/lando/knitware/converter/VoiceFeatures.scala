package sns.lando.knitware.converter

case class VoiceFeatures (enrichedInstruction: EnrichedInstruction)
case class EnrichedInstruction(operatorId: String,
                               orderId: String,
                               serviceId: String,
                               directoryNumber: String,
                               operatorOrderId: String,
                               features: Seq[String])


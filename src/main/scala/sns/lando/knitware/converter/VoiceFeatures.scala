package sns.lando.knitware.converter

case class EnrichedInstruction(modifyVoiceFeaturesInstruction:ModifyVoiceFeaturesInstruction)

case class ModifyVoiceFeaturesInstruction(operatorId: String,
                               orderId: String,
                               serviceId: String,
                               operatorOrderId: String,
                               features: List[String])

case class EnrichedInstruction1(operatorId: String,
                               orderId: String,
                               serviceId: String,
                               directoryNumber: String,
                               operatorOrderId: String,
                               switchServiceId: String,
                               features: Seq[String])

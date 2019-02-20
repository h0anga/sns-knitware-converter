package sns.lando.knitware.converter

case class VoiceFeatures (modifyVoiceFeaturesInstruction: ModifyVoiceFeaturesInstruction)
case class ModifyVoiceFeaturesInstruction (operatorId: String,
                                           orderId: String,
                                           serviceId: String,
                                           operatorOrderId: String,
                                           features: Seq[String])


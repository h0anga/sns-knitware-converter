package sns.lando.knitware.converter

case class EnrichedInstruction(OPERATOR_ID: String,
                               ORDER_ID: String,
                               SERVICE_ID: String,
                               DIRECTORY_NUMBER: String,
                               OPERATOR_ORDER_ID: String,
                               SWITCH_SERVICE_ID: String,
                               FEATURES: Seq[String])


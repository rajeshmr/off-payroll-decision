package uk.gov.hmrc.decisionservice.model

sealed trait DecisionServiceError { def message: String }

case class KnowledgeBaseError(message:String) extends DecisionServiceError

case class RulesFileError(message:String) extends DecisionServiceError

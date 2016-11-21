package uk.gov.hmrc.decisionservice.model

sealed trait DecisionServiceError { def message: String }

case class KnowledgeBaseError(message:String) extends DecisionServiceError

case class RulesFileError(message:String) extends DecisionServiceError

case class RulesFileLoadError(message:String) extends DecisionServiceError {
  def ++(e:RulesFileLoadError):RulesFileLoadError = new RulesFileLoadError(s"${this.message}\n${e.message}")
}

case class FactError(message:String) extends DecisionServiceError

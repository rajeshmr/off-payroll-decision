package uk.gov.hmrc.decisionservice.model

sealed trait DecisionServiceError { def message: String }

case class RulesFileError(message:String) extends DecisionServiceError {
  def ++(e:RulesFileError):RulesFileError = new RulesFileError(s"${this.message}\n${e.message}")
}

case class FactError(message:String) extends DecisionServiceError

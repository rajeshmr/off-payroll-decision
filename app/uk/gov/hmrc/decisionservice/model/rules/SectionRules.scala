package uk.gov.hmrc.decisionservice.model.rules

case class SectionRule(values:List[CarryOver], result:CarryOver)

case class SectionRuleSet(section:String, headings:List[String],rules:List[SectionRule])

sealed trait CarryOver {
  def value:String
  def exit:Boolean
  def equivalent(other:CarryOver) = value.toLowerCase == other.value.toLowerCase || other.value.isEmpty
  def isEmpty = value.isEmpty
}

object NotValidUseCase extends CarryOver {
  override def value = "NotValidUseCase"
  override def exit = false
}

case class >>>(value:String, exit:Boolean = false) extends CarryOver {
  override def toString: String = if (exit) super.toString else s">>>($value)"
}

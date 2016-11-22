package uk.gov.hmrc.decisionservice.model.rules

case class SectionRule(values:List[CarryOver], result:CarryOver)

case class SectionRuleSet(section:String, headings:List[String],rules:List[SectionRule])

sealed trait CarryOver {
  def value:String
  def exit:Boolean
}

object NotValidUseCase extends CarryOver {
  override def value = "NotValidUseCase"
  override def exit = false
}

case class >>>(value:String, exit:Boolean = false) extends CarryOver


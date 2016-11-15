package uk.gov.hmrc.decisionservice.model.rules

case class SectionRule(values:List[String], result:SectionCarryOver)

case class SectionRuleSet(headings:List[String],rules:List[SectionRule])

sealed trait CarryOver {
  def value:String
  def exit:Boolean
}

object SectionNotValidUseCase extends CarryOver {
  override def value = "NotValidUseCase"
  override def exit = false
}

case class SectionCarryOver(value:String, exit:Boolean) extends CarryOver


case class MatrixRule(values:List[SectionCarryOver], result:MatrixDecision)

case class MatrixRuleSet(headings:List[String],rules:List[MatrixRule])

case class MatrixDecision(value:String)

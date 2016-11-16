package uk.gov.hmrc.decisionservice.model.rules

case class SectionRule(values:List[String], result:CarryOverImpl)

case class SectionRuleSet(headings:List[String],rules:List[SectionRule])

sealed trait CarryOver {
  def value:String
  def exit:Boolean
}

object SectionNotValidUseCase extends CarryOver {
  override def value = "NotValidUseCase"
  override def exit = false
}

case class CarryOverImpl(value:String, exit:Boolean) extends CarryOver


case class MatrixRule(values:List[CarryOver], result:MatrixDecision)

case class MatrixRuleSet(headings:List[String],rules:List[MatrixRule])

sealed trait MatrixDecision {
  def value:String
}

object DecisionOutOfIR35 extends MatrixDecision {
  override def value = "OutOfIR35"
}

object DecisionInIR35 extends MatrixDecision {
  override def value = "InIR35"
}

object DecisionNotValidUseCase extends MatrixDecision {
  override def value = "NotValidUseCase"
}

case class MatrixDecisionImpl(value:String) extends MatrixDecision

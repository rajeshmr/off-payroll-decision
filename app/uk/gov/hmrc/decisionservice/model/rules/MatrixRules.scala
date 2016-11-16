package uk.gov.hmrc.decisionservice.model.rules



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

package uk.gov.hmrc.decisionservice.model

case class SectionRule(values:List[String], result:SectionCarryOver)

case class SectionRuleSet(headings:List[String],rules:List[SectionRule])

case class SectionCarryOver(value:String, exit:Boolean)



case class MatrixRule(values:List[SectionCarryOver], result:MatrixDecision)

case class MatrixRuleSet(headings:List[String],rules:List[MatrixRule])

case class MatrixDecision(value:String)

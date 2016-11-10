package uk.gov.hmrc.decisionservice.model

case class SectionFact(question:String, answer:String)

case class SectionFacts(interview:List[SectionFact], sectionName:String){
  def values = interview.map(_.answer)
}

case class SectionRule(values:List[String], result:SectionCarryOver)

case class SectionRules(rows:List[SectionRule])

case class SectionCarryOver(value:String, exit:Boolean)


case class MatrixDecision(value:String)

case class MatrixFact(sectionName:String, carryOver:SectionCarryOver)

case class MatrixFacts(facts:List[MatrixFact]){
  def values = facts.map(_.carryOver)
}

case class MatrixRule(values:List[SectionCarryOver], result:MatrixDecision)


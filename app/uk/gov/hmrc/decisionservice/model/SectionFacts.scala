package uk.gov.hmrc.decisionservice.model

case class SectionFact(question:String, answer:String)

case class SectionFacts(interview:List[SectionFact], sectionName:String)

case class SectionRule(answers:List[String], result:SectionCarryOver)

case class SectionRules(rows:List[SectionRule])

case class SectionCarryOver(value:String, exit:Boolean)


case class MatrixFact(section:String, carryOver:SectionCarryOver)
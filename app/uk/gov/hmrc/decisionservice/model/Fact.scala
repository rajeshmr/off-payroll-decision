package uk.gov.hmrc.decisionservice.model

case class FactRow(question:String, answer:String)

case class Fact(interviewSection:List[FactRow], sectionName:String)

case class RuleRow(answers:List[String], result:SectionResult)

case class Rule(rows:List[RuleRow])

case class SectionResult(value:String, exit:Boolean)


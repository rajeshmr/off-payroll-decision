package uk.gov.hmrc.decisionservice.model

case class Fact(interviewSection:List[(String,String)], sectionName:String) // question -> answer

case class Rule(answers:List[(List[String],SectionResult)]) // permutations of answers

case class SectionResult(value:String, exit:Boolean)


package uk.gov.hmrc.decisionservice.model.rules

case class SectionRule(values:List[CarryOver], result:CarryOver)

case class SectionRuleSet(section:String, headings:List[String],rules:List[SectionRule])

case class Rules(rules:List[SectionRuleSet])

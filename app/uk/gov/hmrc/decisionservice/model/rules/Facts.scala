package uk.gov.hmrc.decisionservice.model.rules

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher

case class Facts(facts:Map[String,CarryOver]){

  def ==+>:(rules:SectionRuleSet):Xor[DecisionServiceError,Facts] = {
    val defaultFactName = rules.section
    SectionFactMatcher.matchFacts(facts,rules) match {
      case x@Xor.Right(_) => x.map(co => Facts(facts + (co.name.getOrElse(defaultFactName) -> co)))
      case e@Xor.Left(_) => e
    }
  }

}


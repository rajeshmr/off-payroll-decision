package uk.gov.hmrc.decisionservice.model.rules

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.ruleengine.FactMatcherInstance

case class Facts(facts:Map[String,CarryOver]){

  def ==+>:(rules:SectionRuleSet):Xor[DecisionServiceError,Facts] = {
    val defaultFactName = rules.section
    FactMatcherInstance.matchFacts(facts,rules).map { carryOver =>
      Facts(facts + (carryOver.name.getOrElse(defaultFactName) -> carryOver))
    }
  }

}


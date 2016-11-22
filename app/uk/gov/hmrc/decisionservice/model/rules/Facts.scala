package uk.gov.hmrc.decisionservice.model.rules

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher

case class Facts(facts:Map[String,CarryOver]){

  def >>>:(rules:SectionRuleSet):Xor[DecisionServiceError,Facts] = {
    SectionFactMatcher.matchFacts(facts,rules) match {
      case x@Xor.Right(_) => x.map(a => Facts(facts + (rules.section -> a)))
      case e@Xor.Left(_) => e
    }
  }

}


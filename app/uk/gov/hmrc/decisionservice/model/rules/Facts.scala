package uk.gov.hmrc.decisionservice.model.rules

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher

case class Facts(facts:Map[String,CarryOver]){

  def >>>:(rules:SectionRuleSet):Xor[DecisionServiceError,Facts] = {
    SectionFactMatcher.matchFacts(facts,rules) match {
      case x@Xor.Right(c) => x.map(a => Facts(facts + (rules.section -> a)))
      case ee@Xor.Left(e) => ee
    }
  }

}


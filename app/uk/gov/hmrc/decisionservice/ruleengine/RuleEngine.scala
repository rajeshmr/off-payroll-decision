package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.rules._

sealed trait RuleEngineDecision {
  def value: String
}

object RuleEngineUndecided extends RuleEngineDecision {
  override def value = "Undecided"
}

case class RuleEngineDecisionImpl(value: String) extends RuleEngineDecision

object FinalFact {
  def unapply(facts: Facts) = facts.facts.values.find(_.exit)
}

object RuleEngine extends App {

  def processRules(rules: Rules, facts: Facts): Xor[DecisionServiceError, RuleEngineDecision] = {
    def go(rules: List[SectionRuleSet], facts: Facts): Xor[DecisionServiceError, Facts] = {
      facts match {
        case FinalFact(_) => Xor.right(facts)
        case _ =>
          rules match {
            case Nil => Xor.right(facts)
            case x :: xs =>
              x >>>: facts match {
                case Xor.Right(newFacts) => go(xs, newFacts)
                case ee@Xor.Left(e) => ee
              }
          }
      }
    }
    val maybeFacts = go(rules.rules, facts)
    maybeFacts match {
      case ee@Xor.Left(e) => ee
      case Xor.Right(facts) => facts match {
        case FinalFact(ff) => Xor.right(RuleEngineDecisionImpl(ff.value))
        case _ => Xor.right(RuleEngineUndecided)
      }
    }
  }


  val facts = Facts(Map(
    "question1" -> >>>("yes"),
    "question2" -> >>>("no" ),
    "question3" -> >>>("yes"),
    "question4" -> >>>("no" ),
    "question6" -> >>>("yes")))
  val sectionRules1 = List(
    SectionRule(List(>>>("yes"), >>>("yes"), >>>("yes")),   >>>("high"  , true)),
    SectionRule(List(>>>("yes"), >>>("no") , >>>("no")) ,   >>>("medium", true)),
    SectionRule(List(>>>("yes"), >>>("no") , >>>("yes")),   >>>("low"   , false)),
    SectionRule(List(>>>("yes"), >>>("")   , >>>("yes")),   >>>("low"   ))
  )
  val sectionRules2 = List(
    SectionRule(List(>>>("no"), >>>("low"   ), >>>("yes")),   >>>("medium"  , true)),
    SectionRule(List(>>>("no"), >>>("medium"), >>>("yes")),   >>>("high"    , true)),
    SectionRule(List(>>>("no"), >>>("high"  ), >>>("yes")),   >>>("low"     , true))
  )
  val ruleSet1 = SectionRuleSet("sectionName1", List("question1", "question2", "question3"), sectionRules1)
  val ruleSet2 = SectionRuleSet("sectionName2", List("question4", "sectionName1", "question6"), sectionRules2)

  val rules = Rules(List(ruleSet1,ruleSet2))

  println(processRules(rules, facts))

}

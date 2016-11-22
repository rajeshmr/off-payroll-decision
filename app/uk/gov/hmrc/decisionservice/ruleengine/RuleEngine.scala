package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.rules._

sealed trait RuleEngineDecision { def value:String }
object RuleEngineUndecided extends RuleEngineDecision { override def value = "Undecided" }
case class RuleEngineDecisionImpl(value:String) extends RuleEngineDecision

object FinalFact {
  def unapply(facts:Facts) = facts.facts.values.find(_.exit)
}

object RuleEngine extends App {

  def processRules(rules:Rules, facts:Facts):Xor[DecisionServiceError,RuleEngineDecision] = {
    def go(rules:List[SectionRuleSet], facts:Facts): Xor[DecisionServiceError,Facts] = {
      facts match {
        case FinalFact(_) => Xor.right(facts)
        case _ =>
          rules match {
            case Nil => Xor.right(facts)
            case x::xs =>
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
    "question1" -> CarryOverImpl("yes", false),
    "question2" -> CarryOverImpl("no", false),
    "question3" -> CarryOverImpl("yes", false)))
  val sectionRules = List(
    SectionRule(List(CarryOverImpl("yes",false),CarryOverImpl("yes",false),CarryOverImpl("yes",false)), CarryOverImpl("high"  , true)),
    SectionRule(List(CarryOverImpl("yes",false),CarryOverImpl("no",false),CarryOverImpl("no",false)), CarryOverImpl("medium", true)),
    SectionRule(List(CarryOverImpl("yes",false),CarryOverImpl("no",false),CarryOverImpl("yes",false)), CarryOverImpl("low"   , true)),
    SectionRule(List(CarryOverImpl("yes",false),CarryOverImpl("",false),CarryOverImpl("yes",false)), CarryOverImpl("low"   , false))
  )
  val ruleSet = SectionRuleSet("sectionName", List("question1", "question2", "question3"), sectionRules)

  val rules = Rules(List(ruleSet))

  println(processRules(rules,facts))

}

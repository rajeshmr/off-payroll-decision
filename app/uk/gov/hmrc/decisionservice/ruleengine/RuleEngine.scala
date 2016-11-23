package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.rules._

import scala.annotation.tailrec

sealed trait RuleEngineDecision {
  def value: String
}

object RuleEngineDecisionUndecided extends RuleEngineDecision {
  override def value = "Undecided"
}

case class RuleEngineDecisionImpl(value: String) extends RuleEngineDecision

object FinalFact {
  def unapply(facts: Facts) = facts.facts.values.find(_.exit)
}

object RuleEngine extends App {
  def processRules(rules: Rules, facts: Facts): Xor[DecisionServiceError, RuleEngineDecision] = {
    @tailrec
    def go(rules: List[SectionRuleSet], facts: Facts): Xor[DecisionServiceError, Facts] = {
      facts match {
        case FinalFact(_) => Xor.right(facts)
        case _ =>
          rules match {
            case Nil => Xor.right(facts)
            case ruleSet :: ruleSets =>
              ruleSet ==+>: facts match {
                case Xor.Right(newFacts) => go(ruleSets, newFacts)
                case e@Xor.Left(_) => e
              }
          }
      }
    }
    val maybeFacts = go(rules.rules, facts)
    maybeFacts match {
      case e@Xor.Left(_) => e
      case Xor.Right(facts) => facts match {
        case FinalFact(ff) => Xor.right(RuleEngineDecisionImpl(ff.value))
        case _ => Xor.right(RuleEngineDecisionUndecided)
      }
    }
  }
}

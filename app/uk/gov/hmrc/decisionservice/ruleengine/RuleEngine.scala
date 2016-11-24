package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import play.api.Logger
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.rules._

import scala.annotation.tailrec

sealed trait RuleEngineDecision {
  def value: String
  def facts: Map[String,CarryOver]
}

case class RuleEngineDecisionUndecided(facts: Map[String,CarryOver]) extends RuleEngineDecision {
  override def value = "Undecided"
}

case class RuleEngineDecisionImpl(value: String, facts: Map[String,CarryOver]) extends RuleEngineDecision

object FinalFact {
  def unapply(facts: Facts) = facts.facts.values.find(_.exit)
}

trait RuleEngine {
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
    maybeFacts.map {
      case f@FinalFact(ff) =>
        Logger.info(s"decision found: '${ff.value}'\n")
        RuleEngineDecisionImpl(ff.value, f.facts)
      case f =>
        Logger.info(s"decision not found - undecided\n")
        RuleEngineDecisionUndecided(f.facts)
    }
  }
}

object RuleEngineInstance extends RuleEngine

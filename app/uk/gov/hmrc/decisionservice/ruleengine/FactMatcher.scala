package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._

import scala.annotation.tailrec



sealed trait FactMatcher {
  type ValueType
  type Facts <: { def values:List[ValueType]}
  type Rule  <: { def values:List[ValueType]; def result:RuleResult}
  type RuleResult

  def matchFacts(facts: Facts, rules: List[Rule]): Xor[DecisionServiceError,RuleResult] =
  {
    @tailrec
    def go(facts: Facts, rules:List[Rule]):Xor[DecisionServiceError,RuleResult] = rules match {
      case Nil => Xor.left(RulesFileError("no match found"))
      case rule :: xs =>
        if (!validateFacts(facts, rule))
          Xor.left(FactError("incorrect fact"))
        else {
          factMatches(facts, rule) match {
            case Some(result) => Xor.right(result)
            case None => go(facts, xs)
          }
        }
    }

    def validateFacts(facts: Facts, rule:Rule):Boolean = facts.values.size == rule.values.size

    def factMatches(facts: Facts, rule:Rule):Option[RuleResult] = {
      facts.values.zip(rule.values).filterNot(equivalent(_)) match {
        case Nil => Some(rule.result)
        case _ => None
      }
    }

    go(facts, rules)
  }

  def equivalent(p:(ValueType,ValueType)):Boolean
}


object SectionFactMatcher extends FactMatcher {
  type ValueType = String
  type Facts = SectionFacts
  type Rule = SectionRule
  type RuleResult = SectionCarryOver

  def equivalent(p:(String,String)):Boolean = p match {
    case (a,b) => a.toLowerCase == b.toLowerCase || a.isEmpty || b.isEmpty
  }
}


object MatrixFactMatcher extends FactMatcher {
  type ValueType = SectionCarryOver
  type Facts = MatrixFacts
  type Rule = MatrixRule
  type RuleResult = MatrixDecision

  def equivalent(p:(SectionCarryOver,SectionCarryOver)):Boolean = p match {
    case (a,b) => a == b || a.value.isEmpty || b.value.isEmpty
  }
}

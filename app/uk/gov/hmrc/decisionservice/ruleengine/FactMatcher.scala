package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, _}

import scala.annotation.tailrec



sealed trait FactMatcher {
  self:EmptyValuesValidator =>

  def matchFacts(facts: Map[String,ValueType], ruleSet: SectionRuleSet): Xor[DecisionServiceError,CarryOver] =
  {
    @tailrec
    def go(factValues: List[ValueType], rules:List[Rule]):Xor[DecisionServiceError,CarryOver] = rules match {
      case Nil => noMatchResult(facts, ruleSet.rules)
      case rule :: xs =>
        if (!validateFacts(factValues, rule))
          Xor.left(FactError("incorrect fact"))
        else {
          factMatches(factValues, rule) match {
            case Some(result) => Xor.right(result)
            case None => go(factValues, xs)
          }
        }
    }

    def validateFacts(factValues: List[ValueType], rule:Rule):Boolean = factValues.size == rule.values.size

    def factMatches(factValues: List[ValueType], rule:Rule):Option[CarryOver] = {
      factValues.zip(rule.values).filterNot(equivalent(_)) match {
        case Nil => Some(rule.result)
        case _ => None
      }
    }

    val factValues = ruleSet.headings.flatMap(a => facts.get(a))
    go(factValues, ruleSet.rules)
  }

  def equivalent(p:(ValueType,ValueType)):Boolean
}


object SectionFactMatcher extends FactMatcher with EmptyValuesValidator {
//  type Rule = SectionRule
//  type CarryOver = CarryOver

  def equivalent(p:(CarryOver,CarryOver)):Boolean = p match {
    case (a,b) => a.value.toLowerCase == b.value.toLowerCase || valueEmpty(b)
  }

  def valueEmpty(v:CarryOver) = v.value.isEmpty

  def notValidUseCase: CarryOver = SectionNotValidUseCase
}


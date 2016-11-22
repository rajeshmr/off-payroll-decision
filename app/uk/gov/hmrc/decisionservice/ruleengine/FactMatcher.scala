package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import play.api.i18n.Messages
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, _}

import scala.annotation.tailrec



sealed trait FactMatcher {
  def matchFacts(facts: Map[String,CarryOver], ruleSet: SectionRuleSet): Xor[DecisionServiceError,CarryOver] =
  {
    @tailrec
    def go(factValues: List[CarryOver], rules:List[SectionRule]):Xor[DecisionServiceError,CarryOver] = rules match {
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

    def validateFacts(factValues: List[CarryOver], rule:SectionRule):Boolean = factValues.size == rule.values.size

    def factMatches(factValues: List[CarryOver], rule:SectionRule):Option[CarryOver] = {
      factValues.zip(rule.values).filterNot(equivalent(_)) match {
        case Nil => Some(rule.result)
        case _ => None
      }
    }

    val factValues = ruleSet.headings.flatMap(a => facts.get(a))
    go(factValues, ruleSet.rules)
  }

  def equivalent(p:(CarryOver,CarryOver)):Boolean = p match {
    case (a,b) => a.value.toLowerCase == b.value.toLowerCase || valueEmpty(b)
  }

  def noMatchResult(facts: Map[String,CarryOver], rules: List[SectionRule]): Xor[DecisionServiceError,CarryOver] = {
    val factSet = factsEmptySet(facts)
    val rulesSet = rulesMaxEmptySet(rules)
    if (factSet.subsetOf(rulesSet)) Xor.Right(NotValidUseCase) else Xor.Left(FactError(Messages("facts.empty.values.error")))
  }

  def emptyPositions(values: Iterable[CarryOver]):Set[Int] = values.zipWithIndex.collect { case (a,i) if(valueEmpty(a)) => i }.toSet

  def factsEmptySet(facts:Map[String,CarryOver]):Set[Int] = emptyPositions(facts.values)

  def rulesMaxEmptySet(rules: List[SectionRule]):Set[Int] = {
    def ruleEmptySet(rules: SectionRule):Set[Int] = emptyPositions(rules.values)
    val sets = for { r <- rules } yield { ruleEmptySet(r) }
    sets.foldLeft(Set[Int]())((a,b) => a ++ b)
  }

  def valueEmpty(c:CarryOver) = c.value.isEmpty
}

object SectionFactMatcher extends FactMatcher


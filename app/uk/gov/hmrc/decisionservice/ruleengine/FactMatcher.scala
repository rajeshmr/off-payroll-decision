package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import play.api.i18n.Messages
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, _}

import scala.annotation.tailrec


sealed trait FactMatcher {
  def matchFacts(facts: Map[String, CarryOver], ruleSet: SectionRuleSet): Xor[DecisionServiceError, CarryOver]
  def noMatchResult(facts: Map[String, CarryOver], rules: List[SectionRule]): Xor[DecisionServiceError, CarryOver]
}


object SectionFactMatcher extends FactMatcher {
  import FactMatcherHelper._

  def matchFacts(facts: Map[String,CarryOver], ruleSet: SectionRuleSet): Xor[DecisionServiceError,CarryOver] =
  {
    @tailrec
    def go(factValues: List[CarryOver], rules:List[SectionRule]):Xor[DecisionServiceError,CarryOver] = rules match {
      case Nil => noMatchResult(facts, ruleSet.rules)
      case rule :: xs if !factsValid(factValues, rule) => Xor.left(FactError(Messages("facts.incorrect.fact.error")))
      case rule :: xs =>
        factMatches(factValues, rule) match {
          case Some(result) => Xor.right(result)
          case None => go(factValues, xs)
        }
    }

    val factValues = ruleSet.headings.map(a => facts.getOrElse(a,EmptyCarryOver))
    go(factValues, ruleSet.rules)
  }

  def factMatches(factValues: List[CarryOver], rule:SectionRule):Option[CarryOver] = {
    factValues.zip(rule.values).filterNot(equivalent(_)) match {
      case Nil => Some(rule.result)
      case _ => None
    }
  }

  def noMatchResult(facts: Map[String,CarryOver], rules: List[SectionRule]): Xor[DecisionServiceError,CarryOver] = {
    val factSet = factsEmptySet(facts)
    val rulesSet = rulesMaxEmptySet(rules)
    if (factSet.subsetOf(rulesSet)) Xor.Right(NotValidUseCase) else Xor.Left(FactError(Messages("facts.empty.values.error")))
  }

}

object FactMatcherHelper {
  def equivalent(p:(CarryOver,CarryOver)):Boolean = p match { case (a,b) => a.equivalent(b) }
  def factsValid(factValues: List[CarryOver], rule:SectionRule):Boolean = factValues.size == rule.values.size
  def emptyPositions(values: Iterable[CarryOver]):Set[Int] = values.zipWithIndex.collect { case (a,i) if(a.isEmpty) => i }.toSet
  def factsEmptySet(facts:Map[String,CarryOver]):Set[Int] = emptyPositions(facts.values)
  def rulesMaxEmptySet(rules: List[SectionRule]):Set[Int] = {
    def ruleEmptySet(rules: SectionRule):Set[Int] = emptyPositions(rules.values)
    val sets = for { r <- rules } yield { ruleEmptySet(r) }
    sets.foldLeft(Set[Int]())((a,b) => a ++ b)
  }
}


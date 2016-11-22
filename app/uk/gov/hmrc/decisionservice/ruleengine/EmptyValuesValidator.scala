package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import play.api.i18n.Messages
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, SectionRule}

trait EmptyValuesValidator {
  type ValueType = CarryOver
  type Rule = SectionRule

  def noMatchResult(facts: Map[String,ValueType], rules: List[Rule]): Xor[DecisionServiceError,CarryOver] = {
    val factSet = factsEmptySet(facts)
    val rulesSet = rulesMaxEmptySet(rules)
    if (factSet.subsetOf(rulesSet)) Xor.Right(notValidUseCase) else Xor.Left(FactError(Messages("facts.empty.values.error")))
  }

  def emptyPositions(values: Iterable[ValueType]):Set[Int] = values.zipWithIndex.collect { case (a,i) if(valueEmpty(a)) => i }.toSet

  def factsEmptySet(facts:Map[String,ValueType]):Set[Int] = emptyPositions(facts.values)

  def rulesMaxEmptySet(rules: List[Rule]):Set[Int] = {
    def ruleEmptySet(rules: Rule):Set[Int] = emptyPositions(rules.values)
    val sets = for { r <- rules } yield { ruleEmptySet(r) }
    sets.foldLeft(Set[Int]())((a,b) => a ++ b)
  }

  def valueEmpty(v:ValueType):Boolean

  def notValidUseCase: CarryOver
}

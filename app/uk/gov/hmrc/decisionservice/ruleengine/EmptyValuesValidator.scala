package uk.gov.hmrc.decisionservice.ruleengine

import uk.gov.hmrc.decisionservice.model._

trait EmptyValuesValidator {
  type ValueType
  type Facts <: { def values:List[ValueType] }
  type Rule  <: { def values:List[ValueType]; def result:RuleResult }
  type RuleResult

  def noMatchError(facts: Facts, rules: List[Rule]): DecisionServiceError = {
    val factSet = factsEmptySet(facts)
    val rulesSet = rulesMaxEmptySet(rules)
    if (factSet.subsetOf(rulesSet)) RulesFileError("rules file is missing match") else FactError("facts have too many empty values")
  }

  def factsEmptySet(facts:Facts):Set[Int] = facts.values.zipWithIndex.collect { case (a,i) if(valueEmpty(a)) => i }.toSet

  def rulesMaxEmptySet(rules: List[Rule]):Set[Int] = {
    def ruleEmptySet(rules: Rule):Set[Int] = rules.values.zipWithIndex.collect { case (a,i) if(valueEmpty(a)) => i }.toSet
    val sets = for { r <- rules } yield { ruleEmptySet(r) }
    sets.foldLeft(Set[Int]())((a:Set[Int],b) => a ++ b)
  }

  def valueEmpty(v:ValueType):Boolean
}

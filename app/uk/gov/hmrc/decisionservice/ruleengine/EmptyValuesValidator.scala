/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.decisionservice.ruleengine

import uk.gov.hmrc.decisionservice.model._

trait EmptyValuesValidator {
  type ValueType
  type Facts = Map[String,ValueType]
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
    sets.foldLeft(Set[Int]())((a,b) => a ++ b)
  }

  def valueEmpty(v:ValueType):Boolean
}

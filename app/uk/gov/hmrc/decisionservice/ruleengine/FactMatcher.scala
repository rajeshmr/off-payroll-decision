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


import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._

import scala.annotation.tailrec



sealed trait FactMatcher {
  self:EmptyValuesValidator =>
  type ValueType
  type Rule  <: { def values:List[ValueType]; def result:RuleResult }
  type RuleResult
  type RuleSet <: { def headings:List[String]; def rules:List[Rule] }

  def matchFacts(facts: Facts, ruleSet: RuleSet): Xor[DecisionServiceError,RuleResult] =
  {
    @tailrec
    def go(factValues: List[ValueType], rules:List[Rule]):Xor[DecisionServiceError,RuleResult] = rules match {
      case Nil => Xor.left(noMatchError(facts, ruleSet.rules))
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

    def factMatches(factValues: List[ValueType], rule:Rule):Option[RuleResult] = {
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
  type ValueType = String
  type Rule = SectionRule
  type RuleResult = SectionCarryOver
  type RuleSet = SectionRuleSet

  def equivalent(p:(String,String)):Boolean = p match {
    case (a,b) => a.toLowerCase == b.toLowerCase || valueEmpty(b)
  }

  def valueEmpty(s:String) = s.isEmpty
}


object MatrixFactMatcher extends FactMatcher with EmptyValuesValidator {
  type ValueType = SectionCarryOver
  type Rule = MatrixRule
  type RuleResult = MatrixDecision
  type RuleSet = MatrixRuleSet

  def equivalent(p:(SectionCarryOver,SectionCarryOver)):Boolean = p match {
    case (a,b) => a.value == b.value || valueEmpty(b)
  }

  def valueEmpty(v:SectionCarryOver) = v.value.isEmpty
}

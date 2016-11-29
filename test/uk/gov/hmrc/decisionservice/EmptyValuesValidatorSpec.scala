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

package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.rules.{SectionCarryOver, SectionRule}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine.EmptyValuesValidator
import uk.gov.hmrc.play.test.UnitSpec

class EmptyValuesValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  object SectionEmptyValuesValidator extends EmptyValuesValidator {
    type ValueType = String
    type Rule = SectionRule
    type RuleResult = SectionCarryOver

    def valueEmpty(s: String) = s.isEmpty
  }

  "empty values validator" should {
    "produce fact error if FactsEmptySet is a subset of MaximumRulesEmptySet" in {
      val fact = Map(
        ("question1" -> "yes"),
        ("question2" -> ""),
        ("question3" -> ""))
      val rules = List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,"yes",""   ), SectionCarryOver("low"   , false))
      )

      val error = SectionEmptyValuesValidator.noMatchError(fact,rules)
      error shouldBe a [FactError]
    }
    "produce rules error if FactsEmptySet is a superset of MaximumRulesEmptySet" in {
      val fact = Map(
        ("question1" -> "yes"),
        ("question2" -> ""),
        ("question3" -> ""))
      val rules = List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,""   ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,""   ,""   ), SectionCarryOver("low"   , false))
      )

      val error = SectionEmptyValuesValidator.noMatchError(fact,rules)
      error shouldBe a [RulesFileError]
    }
  }

}

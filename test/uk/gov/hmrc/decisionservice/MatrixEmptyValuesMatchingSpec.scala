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
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{MatrixDecision, MatrixRule, MatrixRuleSet, SectionCarryOver}
import uk.gov.hmrc.decisionservice.ruleengine.MatrixFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class MatrixEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "matrix fact with empty values matcher" should {
    "produce fact error when fact is missing answers for which rule values are not empty" in {
      val matrixFacts = Map(
        ("BusinessStructure" -> SectionCarryOver("high", true)),
        ("Substitute" -> SectionCarryOver("" , false)),
        ("FinancialRisk" -> SectionCarryOver("" , false))
      )
      val matrixRules = List(
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("high" , true ),SectionCarryOver("low" , true )), MatrixDecision("self employed")),
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("low" , false),SectionCarryOver("low" , true )), MatrixDecision("in IR35")),
        MatrixRule(List(SectionCarryOver("medium", true ),SectionCarryOver("high", true ),SectionCarryOver("low" , true )), MatrixDecision("out of IR35"))
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute", "FinancialRisk"), matrixRules)

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRuleSet)
      println(response)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce rules error when fact is missing answers for which there is no match but corresponding rule values are empty in at least one rule" in {
      val matrixFacts = Map(
        ("BusinessStructure" -> SectionCarryOver("high", true)),
        ("Substitute" -> SectionCarryOver("" , false)),
        ("FinancialRisk" -> SectionCarryOver("" , false))
      )
      val matrixRules = List(
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("high" , true ),SectionCarryOver("low" , true )), MatrixDecision("self employed")),
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("low" , false),SectionCarryOver("low" , true )), MatrixDecision("in IR35")),
        MatrixRule(List(SectionCarryOver("medium", true ),SectionCarryOver("", true ),SectionCarryOver("" , true )), MatrixDecision("out of IR35"))
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute", "FinancialRisk"), matrixRules)

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRuleSet)
      println(response)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
  }

}

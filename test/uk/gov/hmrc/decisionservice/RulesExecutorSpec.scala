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
import play.api.i18n.Messages
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.service.RulesExecutor
import uk.gov.hmrc.play.test.UnitSpec

class RulesExecutorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "processing rules" should {
    "return expected facts" in {
      val model = List(
        Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes")),
        BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))
      )
      val result = RulesExecutor.analyze(model, "rules-test-basic.xls")
      result.isRight shouldBe true
      result map
      { found =>
        val carryOvers = found collect { case x: CarryOver => x }
        val decisions = found collect { case x: Decision => x }
        found should have size (5)
        carryOvers should have size (2)
        decisions should have size (1)
        (carryOvers map {
          _.value
        }) should contain theSameElementsAs List("high", "out")
        (decisions map {
          _.text
        }).head should equal("out of IR35")
      }
    }
    "treat section empty inputs as always matching" in {
      val model = List(
        BusinessStructure(Map("advertiseForWork" -> "no"))
      )
      val result = RulesExecutor.analyze(model, "rules-test-emptyvalues-section.xls")
      result.isRight shouldBe true
      result map { found =>
        val carryOvers = found collect { case x: CarryOver => x }
        found should have size (2)
        carryOvers should have size (1)
        (carryOvers map {
          _.value
        }).head should equal("low")
      }
    }
    "treat decision empty inputs as always matching" in {
      val model = List(
        Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes")),
        BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))
      )
      val result = RulesExecutor.analyze(model, "rules-test-emptyvalues-decision.xls")
      result.isRight shouldBe true
      result map { found =>
        val carryOvers = found collect { case x: CarryOver => x }
        val decisions = found collect { case x: Decision => x }
        found should have size (5)
        carryOvers should have size (2)
        decisions should have size (1)
        (decisions map {
          _.text
        }).head should equal("out")
      }
    }
  }

  "processing not existing spreadsheet" should {
    "produce error" in {
      val result = RulesExecutor.analyze(List(), "not-existing.xls")
      result.isLeft shouldBe true
      result.leftMap(e =>
        e shouldBe a [RulesFileError]
      )
    }
  }

  "processing spreadsheet with errors (missing import)" should {
    "produce error" in {
      val result = RulesExecutor.analyze(List(), "rules-test-missing-import.xls")
      result.isLeft shouldBe true
      result.leftMap(e =>
        (e shouldBe a [KnowledgeBaseError],
         e.message should startWith(Messages("rules.executor.knowledge.base.error")))
      )
    }
  }

}

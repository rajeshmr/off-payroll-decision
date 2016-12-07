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

package uk.gov.hmrc.decisionservice.services

import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.play.test.UnitSpec

class ComplianceTestCasesSpec extends UnitSpec {

  "decision service" should {
    "produce correct the same decision as given by the law test cases" in {
      val facts = ComplianceTestCases.testCases(0)

      val maybeDecision = facts ==>: DecisionServiceInstance
      println(maybeDecision)
      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "outofIR35"
      }
    }
  }
}

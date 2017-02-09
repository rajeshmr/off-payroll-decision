/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.decisionservice.controllers

import uk.gov.hmrc.decisionservice.Versions
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class BusinessStructureAggregatedCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerClusterCsvSpec {
  val clusterName = "businessStructure"
  val BUSINESS_STRUCTURE_SCENARIOS = "/test-scenarios/single/business-structure/scenarios.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision for business structure scenario LOW_0" in {
      createMultipleRequestsSendVerifyDecision(BUSINESS_STRUCTURE_SCENARIOS, Versions.VERSION1)
    }
  }
}

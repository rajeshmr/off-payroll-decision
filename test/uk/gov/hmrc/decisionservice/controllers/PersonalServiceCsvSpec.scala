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

class PersonalServiceCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerClusterCsvSpec {
  val clusterName = "personalService"
  val PERSONAL_SERVICE_SCENARIO_0 = "/test-scenarios/single/personal-service/scenario-0.csv"
  val PERSONAL_SERVICE_SCENARIO_1 = "/test-scenarios/single/personal-service/scenario-1.csv"
  val PERSONAL_SERVICE_SCENARIO_2 = "/test-scenarios/single/personal-service/scenario-2.csv"
  val PERSONAL_SERVICE_SCENARIO_3 = "/test-scenarios/single/personal-service/scenario-3.csv"
  val PERSONAL_SERVICE_SCENARIO_V1 = "/test-scenarios/single/personal-service/scenario-v1.csv"
  val PERSONAL_SERVICE_SCENARIO_V2 = "/test-scenarios/single/personal-service/scenario-v2.csv"
  val PERSONAL_SERVICE_SCENARIO_V3 = "/test-scenarios/single/personal-service/scenario-v3.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision for personal service scenario 0" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_0, Versions.VERSION1)
    }
    "return 200 and correct response with the expected decision for personal service scenario 1" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_1, Versions.VERSION1)
    }
    "return 200 and correct response with the expected decision for personal service scenario 2" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_2, Versions.VERSION1)
    }
    "return 200 and correct response with the expected decision for personal service scenario 3" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_3, Versions.VERSION1)
    }
    "return 200 and correct response with the expected decision for personal service scenario V1" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_V1, Versions.VERSION1)
    }
    "return 200 and correct response with the expected decision for personal service scenario V2" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_V2, Versions.VERSION1)
    }
    "return 200 and correct response with the expected decision for personal service scenario V3" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_V3, Versions.VERSION1)
    }
  }
}

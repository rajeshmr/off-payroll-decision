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
  val PERSONAL_SERVICE_SCENARIO_0_VERSION2 = s"/test-scenarios/${Versions.VERSION100_FINAL}/personal-service/scenario_0.csv"
  val PERSONAL_SERVICE_SCENARIO_0_v111 = s"/test-scenarios/${Versions.VERSION111_FINAL}/personal-service/scenario_0.csv"
  val PERSONAL_SERVICE_SCENARIO_0_LATEST = s"/test-scenarios/${Versions.LATEST}/personal-service/scenario_0.csv"
  val PERSONAL_SERVICE_SCENARIO_1 = "/test-scenarios/single/personal-service/scenario-1.csv"
  val PERSONAL_SERVICE_SCENARIO_2 = "/test-scenarios/single/personal-service/scenario-2.csv"
  val PERSONAL_SERVICE_SCENARIO_3 = "/test-scenarios/single/personal-service/scenario-3.csv"
  val PERSONAL_SERVICE_SCENARIO_V1 = "/test-scenarios/single/personal-service/scenario-v1.csv"
  val PERSONAL_SERVICE_SCENARIO_V2 = "/test-scenarios/single/personal-service/scenario-v2.csv"
  val PERSONAL_SERVICE_SCENARIO_V3 = "/test-scenarios/single/personal-service/scenario-v3.csv"
  val PERSONAL_SERVICE_SCENARIOS_VERSION2 = s"/test-scenarios/${Versions.VERSION100_FINAL}/personal-service/scenarios.csv"
  val PERSONAL_SERVICE_SCENARIOS_v111 = s"/test-scenarios/${Versions.VERSION111_FINAL}/personal-service/scenarios.csv"
  val PERSONAL_SERVICE_SCENARIOS_LATEST = s"/test-scenarios/${Versions.LATEST}/personal-service/scenarios.csv"

  "POST /decide" should {
    //fixme add tests for 1.1.0 and 1.2.0

    s"return 200 and correct response with the expected decision for personal service scenario 0 for version ${Versions.VERSION111_FINAL}" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_0_v111, Versions.VERSION111_FINAL)
    }
    s"return 200 and correct response with the expected decision for personal service scenarios for version ${Versions.VERSION111_FINAL}" in {
      createMultipleRequestsSendVerifyDecision(PERSONAL_SERVICE_SCENARIOS_v111, Versions.VERSION111_FINAL)
    }
  }
}

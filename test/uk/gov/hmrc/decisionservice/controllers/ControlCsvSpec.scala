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

class ControlCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerClusterCsvSpec {
  val clusterName = "control"
  val CONTROL_SCENARIO_0 = "/test-scenarios/single/control/scenario-should-give-MEDIUM-0.csv"
  val CONTROL_SCENARIO_1 = "/test-scenarios/single/control/scenario-should-give-MEDIUM-1.csv"
  val CONTROL_SCENARIO_2 = "/test-scenarios/single/control/scenario-should-give-HIGH.csv"
  val CONTROL_SCENARIO_3 = "/test-scenarios/single/control/scenario-should-give-EXIT-OUT.csv"
  val CONTROL_SCENARIO_0_VERSION2 = s"/test-scenarios/${Versions.VERSION100_FINAL}/control/scenario_0.csv"
  val CONTROL_SCENARIOS_VERSION2 = s"/test-scenarios/${Versions.VERSION100_FINAL}/control/scenarios.csv"
  val CONTROL_SCENARIOS_VERSION110_FINAL = s"/test-scenarios/${Versions.VERSION110_FINAL}/control/scenarios.csv"

  "POST /decide" should {
    s"return 200 and correct response for control scenario 0 for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_0, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response for control scenario 1 for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_1, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response for control scenario 2 for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_2, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response control scenario 3 for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_3, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response control scenario 0 for version ${Versions.VERSION100_FINAL}" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_0_VERSION2, Versions.VERSION100_FINAL)
    }
    s"return 200 and correct response control scenarios for version ${Versions.VERSION100_FINAL}" in {
      createMultipleRequestsSendVerifyDecision(CONTROL_SCENARIOS_VERSION2, Versions.VERSION100_FINAL)
    }
    s"return 200 and correct response control scenarios for version ${Versions.VERSION110_FINAL}" in {
      createMultipleRequestsSendVerifyDecision(CONTROL_SCENARIOS_VERSION110_FINAL, Versions.VERSION110_FINAL)
    }
  }
}

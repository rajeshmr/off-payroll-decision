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

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ControlCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerClusterCsvSpec {
  val clusterName = "control"
  val CONTROL_SCENARIO_0 = "/test-scenarios/single/control/scenario-should-give-MEDIUM-0.csv"
  val CONTROL_SCENARIO_1 = "/test-scenarios/single/control/scenario-should-give-MEDIUM-1.csv"
  val CONTROL_SCENARIO_2 = "/test-scenarios/single/control/scenario-should-give-HIGH.csv"
  val CONTROL_SCENARIO_3 = "/test-scenarios/single/control/scenario-should-give-EXIT-OUT.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision MEDIUM for control scenario 0" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_0)
    }
    "return 200 and correct response with the expected decision MEDIUM for control scenario 1" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_1)
    }
    "return 200 and correct response with the expected decision HIGH for control scenario 2" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_2)
    }
    "return 200 and correct response with the expected decision OutsideIR35 for control scenario 3" in {
      createRequestSendVerifyDecision(CONTROL_SCENARIO_3)
    }
  }
}

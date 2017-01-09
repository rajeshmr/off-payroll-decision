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

/**
  * Created by work on 22/12/2016.
  */
class PartAndParcelCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerCsvSpec {
  val clusterName = "partAndParcel"
  val PART_AND_PARCEL_SCENARIO_0 = "/test-scenarios/single/part-and-parcel/scenario-0.csv"
  val PART_AND_PARCEL_SCENARIO_1 = "/test-scenarios/single/part-and-parcel/scenario-1.csv"
  val PART_AND_PARCEL_SCENARIO_2 = "/test-scenarios/single/part-and-parcel/scenario-2.csv"
  val PART_AND_PARCEL_SCENARIO_3 = "/test-scenarios/single/part-and-parcel/scenario-3.csv"
  val PART_AND_PARCEL_SCENARIO_4 = "/test-scenarios/single/part-and-parcel/scenario-4.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision for part and parcel scenario 0" in {
      createRequestSendVerifyDecision(PART_AND_PARCEL_SCENARIO_0)
    }
    "return 200 and correct response with the expected decision for part and parcel scenario 1" in {
      createRequestSendVerifyDecision(PART_AND_PARCEL_SCENARIO_1)
    }
    "return 200 and correct response with the expected decision for part and parcel scenario 2" in {
      createRequestSendVerifyDecision(PART_AND_PARCEL_SCENARIO_2)
    }
    "return 200 and correct response with the expected decision for part and parcel scenario 3" in {
      createRequestSendVerifyDecision(PART_AND_PARCEL_SCENARIO_3)
    }
    "return 200 and correct response with the expected decision for part and parcel scenario 4" in {
      createRequestSendVerifyDecision(PART_AND_PARCEL_SCENARIO_4)
    }
  }

}

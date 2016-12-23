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

package uk.gov.hmrc.decisionservice.controllers

import play.api.http.Status
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.decisionservice.util.ScenarioReader
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class PersonalServiceAggregatedCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerCsvSpec {
  val clusterName = "personalService"
  val PERSONAL_SERVICE_SCENARIOS = "/test-scenarios/single/personal-service/scenarios.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decisions for personal service scenarios" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIOS)
    }
  }

  override def createRequestSendVerifyDecision(path: String): Unit = {
    val testCasesTry = ScenarioReader.readAggregatedTestCasesTransposed(path)
    testCasesTry.isSuccess shouldBe true
    val testCases = testCasesTry.get
    testCases.map { testCase =>
      val request = testCase.request
      val fakeRequest = FakeRequest(Helpers.POST, "/decide").withBody(toJsonWithValidation(request))
      val result = decisionController.decide()(fakeRequest)
      status(result) shouldBe Status.OK
      val response = jsonBodyOf(await(result))
      verifyResponse(response, testCase.expectedDecision, clusterName)
    }
  }

}

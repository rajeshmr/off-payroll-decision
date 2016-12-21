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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.decisionservice.model.api.DecisionRequest
import uk.gov.hmrc.decisionservice.util.{JsonRequestValidator, ScenarioReader}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class PersonalServiceCsvSpec extends UnitSpec with WithFakeApplication {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val decisionController = DecisionController

  val PERSONAL_SERVICE_SCENARIO_0 = "/test-scenarios/single/scenario_earlyexit_outofir35.csv"
  val PERSONAL_SERVICE_SCENARIO_1 = "/test-scenarios/single/personal-service/scenario_1.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision for personal service scenario 1" in {
      createRequestSendVerifyDecision(PERSONAL_SERVICE_SCENARIO_0)
    }
  }

  def createRequestSendVerifyDecision(path: String): Unit = {
    val testCasesTry = ScenarioReader.readFlattenedTestCaseTransposed(path)
    testCasesTry.isSuccess shouldBe true
    val testCase = testCasesTry.get
    val request = testCase.request
    val fakeRequest = FakeRequest(Helpers.POST, "/decide").withBody(toJsonWithValidation(request))
    val result = decisionController.decide()(fakeRequest)
    status(result) shouldBe Status.OK
    val response = jsonBodyOf(await(result))
    verifyResponse(response, testCase.expectedDecision)
  }

  def verifyResponse(response: JsValue, expectedResult:String): Unit = {
    val version = response \\ "version"
    version should have size 1
    val correlationID = response \\ "correlationID"
    correlationID should have size 1
    val result = response \\ "result"
    result should have size 1
    result(0).as[String] shouldBe expectedResult
  }

  def toJsonWithValidation(request:DecisionRequest):JsValue = {
    val requestJson = Json.toJson(request)
    val requestJsonString = Json.prettyPrint(requestJson)
    val validationResult = JsonRequestValidator.validate(requestJsonString)
    validationResult.isRight shouldBe true
    requestJson
  }

}

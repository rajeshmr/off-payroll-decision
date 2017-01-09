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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.decisionservice.model.api.DecisionRequest
import uk.gov.hmrc.decisionservice.testutil.RequestAndDecision
import uk.gov.hmrc.decisionservice.util.{JsonRequestValidator, JsonResponseValidator}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

trait DecisionControllerCsvSpec extends UnitSpec with WithFakeApplication {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val decisionController = DecisionController
  val clusterName:String

  def createRequestSendVerifyDecision(path: String): Unit = {
    val testCasesTry = RequestAndDecision.readFlattenedTransposed(path)
    testCasesTry.isSuccess shouldBe true
    val testCase = testCasesTry.get
    val request = testCase.request
    val fakeRequest = FakeRequest(Helpers.POST, "/decide").withBody(toJsonWithValidation(request))
    val result = decisionController.decide()(fakeRequest)
    status(result) shouldBe Status.OK
    val response = jsonBodyOf(await(result))
    verifyResponse(response, testCase.expectedDecision, clusterName)
  }

  def createMultipleRequestsSendVerifyDecision(path: String): Unit = {
    val testCasesTry = RequestAndDecision.readAggregatedTransposed(path)
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

  def verifyResponse(response: JsValue, expectedResult:String, clusterName:String): Unit = {
    val responseString = Json.prettyPrint(response)
    val validationResult = JsonResponseValidator.validate(responseString)
    validationResult.isRight shouldBe true
    val version = response \\ "version"
    version should have size 1
    val correlationID = response \\ "correlationID"
    correlationID should have size 1
    val result = response \\ "result"
    result should have size 1
    val resultString = result(0).as[String]
    if (resultString == "Unknown" && expectedResult != "Unknown"){
      val clusterScore = response \\ clusterName
      clusterScore should have size 1
      val clusterScoreString = clusterScore(0).as[String].toLowerCase
      clusterScoreString shouldBe expectedResult.toLowerCase
    }
    else {
      resultString shouldBe expectedResult
    }
  }

  def toJsonWithValidation(request:DecisionRequest):JsValue = {
    val requestJson = Json.toJson(request)
    val requestJsonString = Json.prettyPrint(requestJson)
    val validationResult = JsonRequestValidator.validate(requestJsonString)
    validationResult.isRight shouldBe true
    requestJson
  }

}

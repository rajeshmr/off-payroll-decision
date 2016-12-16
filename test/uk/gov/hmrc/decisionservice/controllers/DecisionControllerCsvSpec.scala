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
import cats.data.Validated
import play.api.http.Status
import play.api.libs.json.Json._
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.decisionservice.Validation
import uk.gov.hmrc.decisionservice.model.FactError
import uk.gov.hmrc.decisionservice.model.api.{DecisionRequest, Score}
import uk.gov.hmrc.decisionservice.model.rules.Facts
import uk.gov.hmrc.decisionservice.ruleengine.{RuleEngineDecision, RulesFileMetaData}
import uk.gov.hmrc.decisionservice.services.DecisionService
import uk.gov.hmrc.decisionservice.util.{JsonValidator, ScenarioReader}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class DecisionControllerCsvSpec extends UnitSpec with WithFakeApplication {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val TEST_CASE_PATH = "/schema/schema_checking_testcase.csv"

  private lazy val testCsvSectionMetadata = List(
    (13, "/tables/control.csv", "control"),
    (24, "/tables/financial_risk.csv", "financial_risk"),
    (5,  "/tables/part_of_organisation.csv", "part_of_organisation"),
    (1,  "/tables/misc.csv", "miscellaneous"),
    (7,  "/tables/business_structure.csv", "business_structure"),
    (13, "/tables/personal_service.csv", "personal_service"),
    (6,  "/tables/matrix_of_matrices.csv", "matrix")
  ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}


  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    lazy val csvSectionMetadata = testCsvSectionMetadata
  }

  object DecisionTestController extends DecisionController {
    lazy val decisionService = DecisionServiceTestInstance
  }

  val decisionController = DecisionTestController


  "POST /decide" should {
    "return 200 and correct response with the expected decision" in {
      val testCasesTry = ScenarioReader.readFlattenedTestCaseTransposed(TEST_CASE_PATH)
      testCasesTry.isSuccess shouldBe true
      val testCase = testCasesTry.get
      val request = testCase.request
      val fakeRequest = FakeRequest(Helpers.POST, "/decide").withBody(toJsonWithValidation(request))
      val result = decisionController.decide()(fakeRequest)
      status(result) shouldBe Status.OK
      val response = jsonBodyOf(await(result))
      verifyResponse(response, testCase.expectedDecision)
    }
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
    val validationResult = JsonValidator.validate(requestJsonString)
    validationResult.isRight shouldBe true
    requestJson
  }

}

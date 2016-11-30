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

import org.specs2.matcher.{MustExpectations, NumericMatchers}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.decisionservice.model.api.DecisionRequest
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.services.DecisionService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class DecisionControllerSpec extends UnitSpec with WithFakeApplication with MustExpectations with NumericMatchers {

  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (13, "/tables/control.csv", "control"),
      (24, "/tables/financial_risk.csv", "financial_risk"),
      (5,  "/tables/part_of_organisation.csv", "part_of_organisation"),
      (1,  "/tables/misc.csv", "miscellaneous"),
      (7,  "/tables/business_structure.csv", "business_structure"),
      (13, "/tables/personal_service.csv", "personal_service"),
      (6,  "/tables/matrix_of_matrices.csv", "matrix")
    ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
  }

  object DecisionTestController extends DecisionController {
    lazy val decisionService = DecisionServiceTestInstance
  }

  val decisionController:DecisionController = DecisionTestController

  val interview = Map("personalService" -> Map("contractualRightForSubstitute" -> "Yes", "contractrualObligationForSubstitute" -> "No", "possibleSubstituteRejection" -> "Yes"))
  val decisionRequest = DecisionRequest("0.0.1-alpha", "12345", interview)

  "POST /decide" should {
    "return 200 and correct response when request is correct" in {
      val fakeRequest = FakeRequest(Helpers.POST, "/decide").withBody(toJson(decisionRequest))
      val result = decisionController.decide()(fakeRequest)
      status(result) shouldBe Status.OK
      println(jsonBodyOf(await(result)))
//      jsonBodyOf(await(result)) shouldBe toJson(RESPONSE_OK)
    }
  }
}

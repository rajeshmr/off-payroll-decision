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

package uk.gov.hmrc.decisionservice

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.play.test.UnitSpec

class DecisionRequestSpec extends UnitSpec {

  val json =
    """
      |{
      |  "version" : "1.0",
      |  "correlationID": "12345",
      |  "personalService":
      |  {
      |    "contractualRightForSubstitute" : "Yes",
      |    "contractrualObligationForSubstitute" : "No",
      |    "possibleSubstituteRejection" : "Yes",
      |    "engagerArrangeWorker" : "Yes",
      |    "contractTermsWorkerPaysSubstitute" : "Yes",
      |    "workerSentActualSubstitiute" : "Yes",
      |    "possibleHelper" : "Yes",
      |    "workerSentActualHelper" : "Yes",
      |    "workerPayActualHelper" : "Yes"
      |  }
      |}
      |
    """.stripMargin

  "decision request json" should {
    "be correctly converted to Scala object" in {
      val parsed = Json.parse(json)
      val jsResult = Json.fromJson[QuestionSet](parsed)
      jsResult.isSuccess shouldBe true
      val obj = jsResult.get
      val m = obj.personalService
      m should have size 9
      val res = List("contractualRightForSubstitute", "contractrualObligationForSubstitute", "possibleSubstituteRejection").flatMap(m.get(_))
      res should contain theSameElementsInOrderAs (List("Yes", "No", "Yes"))
    }
  }

  "decision request Scala object" should {
    "be correctly converted to json object" in {
      val personalServiceQuestions = Map("contractualRightForSubstitute" -> "Yes", "contractrualObligationForSubstitute" -> "No", "possibleSubstituteRejection" -> "Yes")
      val decisionRequest = QuestionSet("0.0.1-alpha", "12345", personalServiceQuestions)
      val jsValue:JsValue = Json.toJson(decisionRequest)
      val personalService = jsValue \\ "personalService"
      val factsWithContractualRight = jsValue \\ "contractualRightForSubstitute"
      personalService should have size 1
      factsWithContractualRight should have size 1
      println(jsValue)
    }
  }

}

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
      |  "sections" : {
      |    "personal-service":
      |    {
      |      "1" : "Yes",
      |      "2" : "No",
      |      "3" : "Yes"
      |    }
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
      obj.sections should have size 1
      val section = obj.sections.get("personal-service")
      section.isDefined shouldBe true
      section.map { m =>
        val res = (1 to 3).flatMap(i => m.get(i.toString))
        res should contain theSameElementsInOrderAs (List("Yes", "No", "Yes"))
      }
    }
  }

  "decision request Scala object" should {
    "be correctly converted to json object" in {
      val personalServiceQuestions = Map("1" -> "Yes", "2" -> "No", "3" -> "Yes")
      val helperQuestions = Map("1" -> "No", "2" -> "No", "3" -> "No")
      val controlQuestions = Map("1" -> "Yes", "2" -> "Yes", "3" -> "Yes")
      val questionSet = Map(
        "personal-service" -> personalServiceQuestions,
        "helper" -> helperQuestions,
        "control" -> controlQuestions
      )
      val decisionRequest = QuestionSet("1.0", questionSet)
      val jsValue:JsValue = Json.toJson(decisionRequest)
      val sections = jsValue \\ "sections"
      val factsWith1 = jsValue \\ "1"
      sections should have size 1
      factsWith1 should have size 3
    }
  }

}

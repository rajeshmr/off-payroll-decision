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
      |      "1" : true,
      |      "2" : false,
      |      "3" : true
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
        res should contain theSameElementsInOrderAs (List(true, false, true))
      }
    }
  }

  "decision request Scala object" should {
    "be correctly converted to json object" in {
      val personalServiceQuestions = Map("1" -> true, "2" -> false, "3" -> true)
      val helperQuestions = Map("1" -> false, "2" -> false, "3" -> false)
      val controlQuestions = Map("1" -> true, "2" -> true, "3" -> true)
      val questionSet = Map(
        "personal-service" -> personalServiceQuestions,
        "helper" -> helperQuestions,
        "control" -> controlQuestions
      )
      val decisionRequest = QuestionSet("1.0", questionSet)
      val jsValue:JsValue = Json.toJson(decisionRequest)
      println(jsValue)
      val sections = jsValue \\ "sections"
      val factsWith1 = jsValue \\ "1"
      sections should have size 1
      factsWith1 should have size 3
    }
  }

}

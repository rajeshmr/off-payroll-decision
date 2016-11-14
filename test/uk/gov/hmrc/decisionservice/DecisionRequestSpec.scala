package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.decisionservice.model.api.{DecisionRequest, Fact, FactSection}
import uk.gov.hmrc.play.test.UnitSpec

class DecisionRequestSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val json =
    """
      |{
      |  "version" : "1.0",
      |  "sections" : [ {
      |    "name" : "personal-service",
      |    "questions" : [ {
      |      "name" : "1",
      |      "answer" : true
      |    }, {
      |      "name" : "2",
      |      "answer" : false
      |    }, {
      |      "name" : "3",
      |      "answer" : true
      |    } ]
      |  } ]
      |}
      |
    """.stripMargin

  "decision request json" should {
    "be correctly converted to Scala object" in {
      val parsed = Json.parse(json)
      val jsResult = Json.fromJson[DecisionRequest](parsed)
      jsResult.isSuccess shouldBe true
      val obj = jsResult.get
      obj.sections should have size 1
      obj.sections(0).questions should have size 3

      val m = obj.sections(0).questions.map(a => (a.name -> a.answer)).toMap
      val res = (1 to 3).flatMap(i => m.get(i.toString))
      res should contain theSameElementsInOrderAs (List(true, false, true))
    }
  }

  "decision request Scala object" should {
    "be correctly converted to json object" in {
      val personalServiceQuestions = List(Fact("1", true),  Fact("2", false), Fact("3", true))
      val helperQuestions = List(Fact("1", false), Fact("2", false), Fact("3", false))
      val controlQuestions = List(Fact("1", true),  Fact("2", true),  Fact("3", true))
      val sections = List(
        FactSection("personal-service", personalServiceQuestions),
        FactSection("helper", helperQuestions),
        FactSection("control", controlQuestions)
      )
      val decisionRequest = DecisionRequest("1.0", sections)
      val jsValue:JsValue = Json.toJson(decisionRequest)
      val jsections = jsValue \\ "sections"
      val jquestions = jsValue \\ "questions"
      jsections should have size 1
      jquestions should have size 3
    }
  }

}

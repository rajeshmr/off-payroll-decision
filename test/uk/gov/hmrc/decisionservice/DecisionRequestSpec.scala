package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.decisionservice.model.api.{Section, QuestionSet}
import uk.gov.hmrc.play.test.UnitSpec

class DecisionRequestSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val json =
    """
      |{
      |  "version" : "1.0",
      |  "sections" : [ {
      |    "name" : "personal-service",
      |    "facts" : {
      |      "1" : true,
      |      "2" : false,
      |      "3" : true
      |    }
      |  } ]
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
      obj.sections(0).facts should have size 3

      val m:Map[String,Boolean] = obj.sections(0).facts
      val res = (1 to 3).flatMap(i => m.get(i.toString))
      res should contain theSameElementsInOrderAs (List(true, false, true))
    }
  }

  "decision request Scala object" should {
    "be correctly converted to json object" in {
      val personalServiceQuestions = Map("1" -> true, "2" -> false, "3" -> true)
      val helperQuestions = Map("1" -> false, "2" -> false, "3" -> false)
      val controlQuestions = Map("1" -> true, "2" -> true, "3" -> true)
      val sections = List(
        Section("personal-service", personalServiceQuestions),
        Section("helper", helperQuestions),
        Section("control", controlQuestions)
      )
      val decisionRequest = QuestionSet("1.0", sections)
      val jsValue:JsValue = Json.toJson(decisionRequest)
      val jsections = jsValue \\ "sections"
      val jfacts = jsValue \\ "facts"
      jsections should have size 1
      jfacts should have size 3
    }
  }

}

package uk.gov.hmrc.decisionservice

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.decisionservice.service.DecisionServiceInstance
import uk.gov.hmrc.play.test.UnitSpec

class DecisionServiceSpec extends UnitSpec {

  "decision service" should {
    "produce correct decision for a sample fact set" in {

      val facts =
      Map(
        "BusinessStructure" -> Map(
                                  "8a" -> "yes",
                                  "8b" -> "yes",
                                  "8c" -> "yes",
                                  "8d" -> "yes",
                                  "8e" -> "no",
                                  "8f" -> "no",
                                  "8g" -> "no"),
        "PersonalService" -> Map(
                                  "2" -> "yes",
                                  "3" -> "yes",
                                  "4" -> "yes",
                                  "5" -> "yes",
                                  "6" -> "yes",
                                  "7" -> "yes",
                                  "8" -> "yes",
                                  "9" -> "yes",
                                  "10" -> "yes")
      )
      val questionSet = QuestionSet("1.0", facts)

      val maybeDecision = DecisionServiceInstance.evaluate(questionSet)

      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "out of IR35"
      }

    }
  }

}

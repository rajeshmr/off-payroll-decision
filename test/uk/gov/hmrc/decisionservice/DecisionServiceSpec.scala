package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.service.DecisionService
import uk.gov.hmrc.play.test.UnitSpec

class DecisionServiceSpec extends UnitSpec {

  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (7, 3, "/business_structure.csv", "BusinessStructure"),
      (9, 2, "/personal_service.csv", "PersonalService"),
      (3, 2, "/matrix.csv", "Matrix")
    ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
  }

  "decision service" should {
    "produce correct decision for a sample fact set leading to section exit" in {
      val facts =
      Facts(Map(
        "8a" -> >>>("yes"),
        "8b" -> >>>("yes"),
        "8c" -> >>>("yes"),
        "8d" -> >>>("yes"),
        "8e" -> >>>("no"),
        "8f" -> >>>("no"),
        "8g" -> >>>("no"),
        "2" -> >>>("yes"),
        "3" -> >>>("yes"),
        "4" -> >>>("yes"),
        "5" -> >>>("yes"),
        "6" -> >>>("yes"),
        "7" -> >>>("yes"),
        "8" -> >>>("yes"),
        "9" -> >>>("yes"),
        "10" -> >>>("yes"))
      )

      val maybeDecision = facts ==>: DecisionServiceTestInstance
      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "exit - out of IR35"
      }
    }
    "produce correct decision for a special custom fact" in {
      val facts =
      Facts(Map(
        "8a" -> >>>("no"),
        "8b" -> >>>("no"),
        "8c" -> >>>("no"),
        "8d" -> >>>("no"),
        "8e" -> >>>("no"),
        "8f" -> >>>("no"),
        "8g" -> >>>("no"),
        "2" -> >>>("yes"),
        "3" -> >>>("no" ),
        "4" -> >>>("yes"),
        "5" -> >>>("yes"),
        "6" -> >>>("yes"),
        "7" -> >>>("no" ),
        "8" -> >>>("yes"),
        "9" -> >>>("yes"),
        "10" -> >>>("yes"))
      )

      val maybeDecision = facts ==>: DecisionServiceTestInstance
      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "specialCase"
      }
    }
  }
}

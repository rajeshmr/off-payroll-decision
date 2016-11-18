package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.service.DecisionService
import uk.gov.hmrc.play.test.UnitSpec

class DecisionServiceErrorSpec extends UnitSpec {

  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    lazy val maybeMatrixRules = loadMatrixRules()

    val csvSectionMetadata = List(
      (7, 2, "business_structure.csv", "BusinessStructure"),
      (9, 2, "personal_service.csv", "PersonalService")
    ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}

    val csvMatrixMetadata = RulesFileMetaData(2, 1, "matrix.csv", "matrix")
  }


  "decision service with initialization error" should {
    "correctly report multiple aggregated error information" in {
      val facts =
      Map(
        "BusinessStructure" -> Map("8a" -> "yes", "8g" -> "no"),
        "PersonalService" -> Map("2" -> "yes", "10" -> "yes")
      )
      val questionSet = QuestionSet("1.0", facts)

      val maybeDecision = DecisionServiceTestInstance.evaluate(questionSet)

      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error.message shouldBe "resource not found: business_structure.csv resource not found: personal_service.csv"
      }
    }
  }

}

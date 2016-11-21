package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.service.DecisionService
import uk.gov.hmrc.play.test.UnitSpec

class DecisionServiceErrorSpec extends UnitSpec {

  object DecisionServiceNotExistingCsvTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    lazy val maybeMatrixRules = loadMatrixRules()

    val csvSectionMetadata = List(
      (7, 2, "business_structure_not_existing.csv", "BusinessStructure"),
      (9, 2, "personal_service_not_existing.csv", "PersonalService")
    ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
    val csvMatrixMetadata = RulesFileMetaData(2, 1, "/matrix.csv", "matrix")
  }

  object DecisionServiceCsvWithErrorsTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    lazy val maybeMatrixRules = loadMatrixRules()

    val csvSectionMetadata = List(
      (7, 2, "/business_structure_errors.csv", "BusinessStructure"),
      (9, 2, "/personal_service_errors.csv", "PersonalService")
    ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
    val csvMatrixMetadata = RulesFileMetaData(2, 1, "/matrix.csv", "matrix")
  }

  object DecisionServiceCsvWithBadMetadataTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    lazy val maybeMatrixRules = loadMatrixRules()

    val csvSectionMetadata = List(
    (17, 5, "/business_structure.csv", "BusinessStructure"),
    (19, 5, "/personal_service.csv", "PersonalService")
    ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
    val csvMatrixMetadata = RulesFileMetaData(12, 20, "/matrix.csv", "matrix")
  }

  val facts =
    Map(
      "BusinessStructure" -> Map("8a" -> "yes", "8g" -> "no"),
      "PersonalService" -> Map("2" -> "yes", "10" -> "yes")
    )
  val questionSet = QuestionSet("1.0", facts)


  "decision service with initialization error" should {
    "correctly report multiple aggregated error information" in {
      val maybeDecision = DecisionServiceNotExistingCsvTestInstance.evaluate(questionSet)
      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error.message.contains("business_structure_not_existing.csv") shouldBe true
        error.message.contains("personal_service_not_existing.csv") shouldBe true
      }
    }
    "correctly report errors in csv files" in {
      val maybeDecision = DecisionServiceCsvWithErrorsTestInstance.evaluate(questionSet)
      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error shouldBe a [DecisionServiceError]
        error.message.contains("line") shouldBe true
      }
    }
    "correctly report errors in metadata files" in {
      val maybeDecision = DecisionServiceCsvWithBadMetadataTestInstance.evaluate(questionSet)
      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error shouldBe a [DecisionServiceError]
        error.message.contains("line") shouldBe true
      }
    }
  }

}

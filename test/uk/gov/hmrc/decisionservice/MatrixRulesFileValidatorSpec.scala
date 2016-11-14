package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec

class MatrixRulesFileValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  var headers = List("Section1", "Section2", "Section3", "Section4")
  var result = List("Decision")
  var validHeaderMetaData: RulesFileMetaData = RulesFileMetaData(headers, result, "", 4, 1)

  var metaData_HeaderSizeMismatch: RulesFileMetaData = RulesFileMetaData(headers, result, "", 9, 1)

  var validAnswers = List("Low", "Medium", "", "High")
  var validAnswers_result = List("In IR35")
  var validAnswerMetaData: RulesFileMetaData = RulesFileMetaData(validAnswers, validAnswers_result, "", 4, 1)

  var invalidAnswers = List("medium", "Bob", "low", "")
  var metaData_withInvalidAnswer: RulesFileMetaData = RulesFileMetaData(invalidAnswers, validAnswers_result, "", 4, 1)


  var invalidAnswers_decision = List("whatever")
  var metaData_withInvalidCarryOver: RulesFileMetaData = RulesFileMetaData(validAnswers, invalidAnswers_decision, "", 4, 1)

  var metaData_withInvalidResultSize: RulesFileMetaData = RulesFileMetaData(validAnswers, validAnswers_result, "", 4, 12)


  "section rules file validator" should {

    "validate correct column header size" in {
      val mayBeValid = MatrixRuleValidator.validateColumnHeaders(validHeaderMetaData)
      mayBeValid.isRight shouldBe true
    }

    "return error for invalid column header size" in {
      val mayBeValid = MatrixRuleValidator.validateColumnHeaders(metaData_HeaderSizeMismatch)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Column header size does not match metadata"
      }
    }

    "correctly validate valid rule row" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(validAnswerMetaData)
      mayBeValid.isRight shouldBe true

    }

    "return error for invalid rule text" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(metaData_withInvalidAnswer)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid answer"
      }
    }

    "return error for invalid decision text" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(metaData_withInvalidCarryOver)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid Decision value on row x"
      }
    }

  }

}

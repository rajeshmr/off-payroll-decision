package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec

class MatrixRulesFileValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  var headers = List("Section1", "Section2", "Section3", "Section4")
  var result = List("Decision")

  var validHeaderRow = headers ::: result
  var validHeaderMetaData: RulesFileMetaData = RulesFileMetaData(4, 1, "")

  var metaData_HeaderSizeMismatch: RulesFileMetaData = RulesFileMetaData(9, 1, "")

  var validAnswers = List("Low", "Medium", "", "High")
  var validAnswers_result = List("In IR35")

  var validRuleRow = validAnswers ::: validAnswers_result
  var validAnswerMetaData: RulesFileMetaData = RulesFileMetaData(4, 1, "")

  var answerMetaDataSizeMismatch: RulesFileMetaData = RulesFileMetaData(41, 1, "")

  var invalidAnswers = List("medium", "Bob", "low", "")

  var ruleRowWithInvalidRuleText = invalidAnswers ::: validAnswers_result
  var metaData_withInvalidAnswer: RulesFileMetaData = RulesFileMetaData(4, 1, "")


  var invalidAnswers_decision = List("whatever")

  var ruleRowWithInvalidDecision = validAnswers ::: invalidAnswers_decision
  var metaData_withInvalidCarryOver: RulesFileMetaData = RulesFileMetaData(4, 1, "")


  "section rules file validator" should {

    "validate correct column header size" in {
      val mayBeValid = MatrixRuleValidator.validateColumnHeaders(validHeaderRow, validHeaderMetaData)
      mayBeValid.isRight shouldBe true
    }

    "return error for invalid column header size" in {
      val mayBeValid = MatrixRuleValidator.validateColumnHeaders(validHeaderRow, metaData_HeaderSizeMismatch)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Column header size does not match metadata"
      }
    }

    "correctly validate valid rule row" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(validRuleRow, validAnswerMetaData, 3)
      mayBeValid.isRight shouldBe true

    }

    "return error for rule row size mismatch" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(validRuleRow, answerMetaDataSizeMismatch, 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Row size does not match metadata on row 3"
      }
    }

    "return error for invalid rule text" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(ruleRowWithInvalidRuleText, metaData_withInvalidAnswer, 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid CarryOver value on row 3"
      }
    }

    "return error for invalid decision text" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(ruleRowWithInvalidDecision, metaData_withInvalidCarryOver, 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid Decision value on row 2"
      }
    }

  }

}

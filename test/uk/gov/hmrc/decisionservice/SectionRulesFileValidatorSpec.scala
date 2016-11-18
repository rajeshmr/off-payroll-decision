package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.RulesFileError
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec

class SectionRulesFileValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  var headers = List("Q1", "Q2", "Q3", "Q4")
  var resultPair = List("CarryOver", "Exit")

  var headerRow = headers ::: resultPair
  var validHeaderMetaData: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")

  var metaData_HeaderSizeMismatch: RulesFileMetaData = RulesFileMetaData(9, 2, "", "")


  var validAnswers = List("Yes", "No", "Yes", "")
  var validAnswers_resultPair = List("Low", "false")

  var validRuleRow = validAnswers ::: validAnswers_resultPair

  var validAnswerMetaData: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")

  var answerMetaDataSizeMismatch: RulesFileMetaData = RulesFileMetaData(23, 2, "", "")

  var invalidAnswers = List("Yes", "Bob", "Yes", "")

  var ruleRowWithInvalidRuleText = invalidAnswers ::: validAnswers_resultPair
  var metaData_withInvalidAnswer: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")


  var invalidAnswers_carryOver = List("whatever", "true")

  var ruleRowWithInvalidCarryOverText = validAnswers ::: invalidAnswers_carryOver
  var metaData_withInvalidCarryOver: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")

  var invalidAnswers_exit = List("low", "wrong!!!")
  var ruleRowWithInvalidExitText = validAnswers ::: invalidAnswers_exit
  var metaData_withInvalidExit: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")


  "section rules file validator" should {

    "validate correct column header size" in {
      val mayBeValid = SectionRuleValidator.validateColumnHeaders(headerRow, validHeaderMetaData)
      mayBeValid.isRight shouldBe true
    }

    "return error for invalid column header size" in {
      val mayBeValid = SectionRuleValidator.validateColumnHeaders(headerRow, metaData_HeaderSizeMismatch)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Column header size does not match metadata"
      }
    }

    "correctly validate valid rule row" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(validRuleRow, validAnswerMetaData, 3)
      mayBeValid.isRight shouldBe true

    }

    "return error for invalid rule text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(ruleRowWithInvalidRuleText, metaData_withInvalidAnswer, 4)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid answer value on row 4"
      }
    }

    "return error for invalid caryOver text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(ruleRowWithInvalidCarryOverText, metaData_withInvalidCarryOver, 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid CarryOver value for row 2"
      }
    }


    "return error for rule row size mismatch" in {
      val mayBeValid = MatrixRuleValidator.validateRuleRow(validRuleRow, answerMetaDataSizeMismatch, 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Row size does not match metadata on row 3"
      }
    }

    "return error for invalid exit text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(ruleRowWithInvalidExitText, metaData_withInvalidExit, 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid Exit value for row 2"
      }

    }

  }

}

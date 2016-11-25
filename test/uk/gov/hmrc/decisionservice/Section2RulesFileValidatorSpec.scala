package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.decisionservice.ruleengine.SectionRuleValidator._


class Section2RulesFileValidatorSpec extends UnitSpec {

  object RowFixture {
    private val headers = List("Section1", "Section2", "Section3", "Section4")
    private val result = List("Decision")
    private val validAnswers = List("Low", "Medium", "", "High")
    private val validAnswers_result = List("In IR35")
    private val invalidAnswers = List("medium", "Bob", "low", "")
    private val invalidAnswers_decision = List("whatever")
    val validHeaderRow = headers ::: result
    val validRuleRow = validAnswers ::: validAnswers_result
    val ruleRowWithInvalidRuleText = invalidAnswers ::: validAnswers_result
    val ruleRowWithInvalidDecision = validAnswers ::: invalidAnswers_decision
  }

  object RowFixture2 {
    val headers = List("Q1", "Q2", "Q3", "Q4")
    val resultPair = List("CarryOver", "Exit")
    val headerRow = headers ::: resultPair

    val validAnswers = List("Yes", "No", "Yes", "")
    val validAnswers_resultPair = List("Low", "false")

    val validRuleRow = validAnswers ::: validAnswers_resultPair

    val invalidAnswers = List("Yes", "Bob", "Yes", "")
    val ruleRowWithInvalidRuleText = invalidAnswers ::: validAnswers_resultPair

    val invalidAnswers_carryOver = List("whatever", "true")
    val ruleRowWithInvalidCarryOverText = validAnswers ::: invalidAnswers_carryOver

    val invalidAnswers_exit = List("low", "wrong!!!")
    val ruleRowWithInvalidExitText = validAnswers ::: invalidAnswers_exit
  }

  object MetadataFixture {
    val valid = RulesFileMetaData(4, 1, "", "")
    val headerSizeMismatch = RulesFileMetaData(9, 1, "", "")
    val validAnswer = RulesFileMetaData(4, 1, "", "")
    val answerSizeMismatch = RulesFileMetaData(41, 1, "", "")
    val invalidAnswer = RulesFileMetaData(4, 1, "", "")
    val invalidCarryOver = RulesFileMetaData(4, 1, "", "")
  }

  object MetadataFixture2 {
    val validHeaderMetaData: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")
    val metaData_HeaderSizeMismatch: RulesFileMetaData = RulesFileMetaData(9, 2, "", "")
    val validAnswerMetaData: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")
    val metaData_withInvalidAnswer: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")
    val metaData_withInvalidCarryOver: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")
    val metaData_withInvalidExit: RulesFileMetaData = RulesFileMetaData(4, 2, "", "")

  }


  "section rules file validator" should {

    "validate correct column header size" in {
      val mayBeValid = validateColumnHeaders(RowFixture.validHeaderRow, MetadataFixture.valid)
      mayBeValid.isRight shouldBe true
    }

    "validate correct column header size (2)" in {
      val mayBeValid = validateColumnHeaders(RowFixture2.headerRow, MetadataFixture2.validHeaderMetaData)
      mayBeValid.isRight shouldBe true
    }

    "return error for invalid column header size" in {
      val mayBeValid = validateColumnHeaders(RowFixture.validHeaderRow, MetadataFixture.headerSizeMismatch)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Column header size does not match metadata"
      }
    }

    "return error for invalid column header size (2)" in {
      val mayBeValid = validateColumnHeaders(RowFixture2.headerRow, MetadataFixture2.metaData_HeaderSizeMismatch)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Column header size does not match metadata"
      }
    }

    "return error for rule row size mismatch" in {
      val mayBeValid = validateRuleRow(RowFixture.validRuleRow, MetadataFixture.answerSizeMismatch, 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Row size does not match metadata on row 3"
      }
    }

    "correctly validate valid rule row" in {
      val mayBeValid = validateRuleRow(RowFixture2.validRuleRow, MetadataFixture2.validAnswerMetaData, 3)
      mayBeValid.isRight shouldBe true

    }

    "return error for invalid rule text" in {
      val mayBeValid = validateRuleRow(RowFixture2.ruleRowWithInvalidRuleText, MetadataFixture2.metaData_withInvalidAnswer, 4)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid answer value on row 4"
      }
    }

    "return error for invalid caryOver text" in {
      val mayBeValid = validateRuleRow(RowFixture2.ruleRowWithInvalidCarryOverText, MetadataFixture2.metaData_withInvalidCarryOver, 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid CarryOver value for row 2"
      }
    }

    "return error for invalid exit text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(RowFixture2.ruleRowWithInvalidExitText, MetadataFixture2.metaData_withInvalidExit, 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid Exit value for row 2"
      }

    }

  }

}

package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.decisionservice.ruleengine.SectionRuleValidator._


/**
  * TODO merge it with SectionRulesFileValidatorSpec
  */
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

  object MetadataFixture {
    val valid = RulesFileMetaData(4, 1, "", "")
    val headerSizeMismatch = RulesFileMetaData(9, 1, "", "")
    val validAnswer = RulesFileMetaData(4, 1, "", "")
    val answerSizeMismatch = RulesFileMetaData(41, 1, "", "")
    val invalidAnswer = RulesFileMetaData(4, 1, "", "")
    val invalidCarryOver = RulesFileMetaData(4, 1, "", "")
  }

  "section rules file validator" should {

    "validate correct column header size" in {
      val mayBeValid = SectionRuleValidator.validateColumnHeaders(RowFixture.validHeaderRow, MetadataFixture.valid)
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

    "return error for rule row size mismatch" in {
      val mayBeValid = validateRuleRow(RowFixture.validRuleRow, MetadataFixture.answerSizeMismatch, 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Row size does not match metadata on row 3"
      }
    }
  }

}

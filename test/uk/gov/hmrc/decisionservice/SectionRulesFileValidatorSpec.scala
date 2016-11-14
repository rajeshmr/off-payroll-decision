package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{SectionCarryOver, _}
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec

class SectionRulesFileValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  var headers = List("Q1", "Q2", "Q3", "Q4")
  var resultPair = List("CarryOver", "Exit")
  var validHeaderMetaData: RulesFileMetaData = RulesFileMetaData(headers, resultPair, "", 4, 2)
  var metaData_HeaderSizeMismatch: RulesFileMetaData = RulesFileMetaData(headers, resultPair, "", 9, 2)


  var validAnswers = List("Yes", "No", "Yes", "")
  var validAnswers_resultPair = List("Low", "false")
  var validAnswerMetaData: RulesFileMetaData = RulesFileMetaData(validAnswers, validAnswers_resultPair, "", 4, 2)

  var invalidAnswers = List("Yes", "Bob", "Yes", "")
  var metaData_withInvalidAnswer: RulesFileMetaData = RulesFileMetaData(invalidAnswers, validAnswers_resultPair, "", 4, 2)


  var invalidAnswers_carryOver = List("whatever", "true")
  var metaData_withInvalidCarryOver: RulesFileMetaData = RulesFileMetaData(validAnswers, invalidAnswers_carryOver, "", 4, 2)

  var invalidAnswers_exit = List("low", "wrong!!!")
  var metaData_withInvalidExit: RulesFileMetaData = RulesFileMetaData(validAnswers, invalidAnswers_exit, "", 4, 2)

  var metaData_withInvalidResultPairSize: RulesFileMetaData = RulesFileMetaData(validAnswers, validAnswers_resultPair, "", 4, 12)


  "section rules file validator" should {

    "validate correct column header size" in {
      val mayBeValid = SectionRuleValidator.validateColumnHeaders(validHeaderMetaData)
      mayBeValid.isRight shouldBe true
    }

    "return error for invalid column header size" in {
      val mayBeValid = SectionRuleValidator.validateColumnHeaders(metaData_HeaderSizeMismatch)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Column header size does not match metadata"
      }
    }

    "correctly validate valid rule row" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(validAnswerMetaData)
      mayBeValid.isRight shouldBe true

    }

    "return error for invalid rule text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(metaData_withInvalidAnswer)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid answer"
      }
    }

    "return error for invalid caryOver text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(metaData_withInvalidCarryOver)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid CarryOver value for row x"
      }
    }

    "return error for invalid exit text" in {
      val mayBeValid = SectionRuleValidator.validateRuleRow(metaData_withInvalidExit)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid Exit value for row x"
      }

    }

  }

}

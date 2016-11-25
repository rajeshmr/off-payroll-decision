package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.decisionservice.ruleengine.SectionRuleValidator._


class SectionRulesFileValidatorSpec extends UnitSpec {

  val valueHeaders = List("Q1", "Q2", "Q3", "Q4")
  val resultHeaders = List("CarryOver", "Exit")

  val validRuleValues1 = List("Low", "Medium", "", "High")
  val validRuleValues2 = List("Yes", "No", "Yes", "")
  val invalidRuleValues = List("Yes", "Bob", "Yes", "")

  val validNonExitResultColumns1 = List("In IR35")
  val validNonExitResultColumns2 = List("Low", "false")
  val invalidExitResultColumns = List("whatever", "true")
  val invalidResultColumns = List("low", "wrong!!!")

  "section rules file validator" should {

    "validate correct column header size" in {
      val (v,r) = (valueHeaders,resultHeaders)
      val mayBeValid = validateColumnHeaders(v ::: r, RulesFileMetaData(v.size, r.size, "", ""))
      mayBeValid.isRight shouldBe true
    }
    "return error for invalid column header size" in {
      val (v,r) = (valueHeaders,resultHeaders)
      val mayBeValid = validateColumnHeaders(v ::: r, RulesFileMetaData(v.size, r.size+1, "", ""))
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
      }
    }
    "return error for rule row size mismatch" in {
      val (v,r) = (validRuleValues1,validNonExitResultColumns1)
      val mayBeValid = validateRuleRow(v ::: r, RulesFileMetaData(41, 1, "", ""), 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Row size does not match metadata on row 3"
      }
    }
    "correctly validate valid rule row" in {
      val (v,r) = (validRuleValues2,validNonExitResultColumns2)
      val mayBeValid = validateRuleRow(v ::: r, RulesFileMetaData(v.size, r.size, "", ""), 3)
      mayBeValid.isRight shouldBe true
    }
    "return error for invalid rule text" in {
      val (v,r) = (invalidRuleValues,validNonExitResultColumns2)
      val mayBeValid = validateRuleRow(v ::: r, RulesFileMetaData(v.size, r.size, "", ""), 4)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid answer value on row 4"
      }
    }
    "return error for invalid caryOver text" in {
      val (v,r) = (validRuleValues2,invalidExitResultColumns)
      val mayBeValid = validateRuleRow(v ::: r, RulesFileMetaData(v.size, r.size, "", ""), 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid CarryOver value for row 2"
      }
    }
    "return error for invalid exit text" in {
      val (v,r) = (validRuleValues2,invalidResultColumns)
      val mayBeValid = validateRuleRow(v ::: r, RulesFileMetaData(v.size, r.size, "", ""), 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "Invalid Exit value for row 2"
      }
    }
  }
}

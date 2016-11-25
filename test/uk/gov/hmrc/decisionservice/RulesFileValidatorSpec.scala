package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileLineValidatorInstance._


class RulesFileValidatorSpec extends UnitSpec {

  "section rules file validator" should {
    "validate correct column header size" in {
      val (v,r) = (List("Q1", "Q2", "Q3", "Q4"), List("CarryOver", "Exit"))
      val mayBeValid = validateColumnHeaders(v ::: r, RulesFileMetaData(v.size, "", ""))
      mayBeValid.isRight shouldBe true
    }
    "return error for invalid column header size" in {
      val (v,r) = (List("Q1", "Q2", "Q3", "Q4"), List("CarryOver", "Exit"))
      val mayBeValid = validateColumnHeaders(v, RulesFileMetaData(v.size+1, "", ""))
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
      }
    }
    "return error for rule row size mismatch" in {
      val (v,r) = (List("Low", "Medium", "", "High"), List("In IR35"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(41, "path", ""), 3)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "row size is 5, expected greater than 41 in row 3 in file path"
      }
    }
    "correctly validate valid rule row" in {
      val (v,r) = (List("Yes", "No", "Yes", ""), List("Low", "false"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "", ""), 3)
      mayBeValid.isRight shouldBe true
    }
    "return error for invalid rule text" in {
      val (v,r) = (List("Yes", "Bob", "Yes", ""), List("Low", "false"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "path", ""), 4)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "invalid value in row 4 in file path"
      }
    }
    "return error for invalid carry over text" in {
      val (v,r) = (List("Yes", "No", "Yes", ""), List("whatever", "true"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "path", ""), 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "invalid carry over value whatever in row 2 in file path"
      }
    }
    "return error when carry over is missing completely" in {
      val (v,r) = (List("Yes", "No", "Yes", ""), List())
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "path", ""), 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "row size is 4, expected greater than 4 in row 2 in file path"
      }
    }
    "return no error if only the carry over value is provided and exit value and fact name values are missing" in {
      val (v,r) = (List("Yes", "No", "Yes", ""), List("medium"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "", ""), 2)
      mayBeValid.isRight shouldBe true
    }
    "return no error if carry over is fully provided and there are extra cells" in {
      val (v,r) = (List("Yes", "No", "Yes", ""), List("medium", "false", "factName", "extra"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "", ""), 2)
      mayBeValid.isRight shouldBe true
    }
    "return error for invalid exit text" in {
      val (v,r) = (List("Yes", "No", "Yes", ""), List("low", "wrong!!!"))
      val mayBeValid = validateLine(v ::: r, RulesFileMetaData(v.size, "path", ""), 2)
      mayBeValid.isLeft shouldBe true
      mayBeValid.leftMap { error =>
        error shouldBe a[RulesFileError]
        error.message shouldBe "invalid exit value in row 2 in file path"
      }
    }
  }
}

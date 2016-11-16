package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.RulesFileLoadError
import uk.gov.hmrc.decisionservice.model.rules.CarryOverImpl
import uk.gov.hmrc.decisionservice.ruleengine.{MatrixFactMatcher, MatrixRulesLoader, RulesFileMetaData}
import uk.gov.hmrc.play.test.UnitSpec

class MatrixRulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/matrix.csv"
  val csvFilePathError = "/matrix_error.csv"
  val csvMetadata = RulesFileMetaData(2, 1, csvFilePath)
  val csvMetadataError = RulesFileMetaData(2, 1, csvFilePathError)

  "matrix rules loader" should {
    "load matrix rules from a csv file" in {
      val maybeRules = MatrixRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size 3
        ruleset.headings should have size 2
      }
    }
    "return error if file is not found" in {
      val maybeRules = MatrixRulesLoader.load(RulesFileMetaData(2, 1, csvFilePath + "xx"))
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return error if file contains invalid data" in {
      val maybeRules = MatrixRulesLoader.load(csvMetadataError)
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "provide valid input for an inference against fact" in {
      val matrixFacts = Map(
        ("BusinessStructure" -> CarryOverImpl("high", true)), ("Substitute" -> CarryOverImpl("high" , false))
      )
      val maybeRules = MatrixRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size 3
        ruleset.headings should have size 2
        val response = MatrixFactMatcher.matchFacts(matrixFacts, ruleset)
        response.isRight shouldBe true
        response.map { decision =>
          decision.value should equal("out of IR35")
        }
      }
    }
  }
}


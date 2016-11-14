package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{MatrixFact, MatrixFacts, RulesFileLoadError, SectionCarryOver}
import uk.gov.hmrc.decisionservice.ruleengine.{MatrixFactMatcher, MatrixRulesLoader, RulesFileMetaData}
import uk.gov.hmrc.play.test.UnitSpec

class MatrixRulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/matrix.csv"
  val csvFilePathError = "/matrix_error.csv"
  val csvMetadata = RulesFileMetaData(List("Section1", "Section2"), List("Decision"), csvFilePath, 4, 2)
  val csvMetadataError = RulesFileMetaData(List("Section1", "Section2"), List("Decision"), csvFilePathError, 4, 2)

  "matrix rules loader" should {
    "load matrix rules from a csv file" in {
      val maybeRules = MatrixRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size (3)
      }
    }
    "return error if file is not found" in {
      val maybeRules = MatrixRulesLoader.load(RulesFileMetaData(List("Section1","Section2"), List("Decision"), csvFilePath + "xx", 4, 2))
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
      val matrixFacts = MatrixFacts(List(
        MatrixFact("BusinessStructure", SectionCarryOver("high", true)), MatrixFact("Substitute", SectionCarryOver("high" , false))
      ))
      val maybeRules = MatrixRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size (3)
        val response = MatrixFactMatcher.matchFacts(matrixFacts, ruleset.rules)
        response.isRight shouldBe true
        response.map { decision =>
          decision.value should equal("out of IR35")
        }
      }
    }
  }
}


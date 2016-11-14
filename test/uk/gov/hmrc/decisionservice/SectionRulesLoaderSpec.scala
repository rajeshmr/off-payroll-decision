package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{RulesFileLoadError, SectionFact, SectionFacts}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec

class SectionRulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/business_structure.csv"
  val csvFilePathError = "/business_structure_error.csv"
  val csvMetadata = RulesFileMetaData(List("Q1","Q2","Q3"), List("CO1","CO2"), csvFilePath, 4, 2)
  val csvMetadataError = RulesFileMetaData(List("Q1","Q2","Q3"), List("CO1","CO2"), csvFilePathError, 4, 2)

  "section rules loader" should {
    "load section rules from a csv file" in {
      val maybeRules = SectionRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size (4)
      }
    }
    "return error if file is not found" in {
      val maybeRules = SectionRulesLoader.load(RulesFileMetaData(List("Q1","Q2","Q3"), List("CO1","CO2"), csvFilePath + "xx", 4, 2))
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return error if file contains invalid data" in {
      val maybeRules = SectionRulesLoader.load(csvMetadataError)
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "provide valid input for an inference against fact" in {
      val fact = SectionFacts(List(
        SectionFact("question1", "yes"),
        SectionFact("question2", "no"),
        SectionFact("question3", "yes")),"BusinessStructure")
      val maybeRules = SectionRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size (4)
        val response = SectionFactMatcher.matchFacts(fact, ruleset.rules)
        response.isRight shouldBe true
        response.map { sectionResult =>
          sectionResult.value should equal("low")
          sectionResult.exit should equal(true)
        }
      }
    }
  }
}


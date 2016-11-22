package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.RulesFileLoadError
import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec

class SectionRulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/section_rules_sample.csv"
  val csvFilePathError = "/section_rules_sample_error.csv"
  val csvMetadata = RulesFileMetaData(3, 2, csvFilePath, "sectionName")
  val csvMetadataError = RulesFileMetaData(3, 2, csvFilePathError, "sectionName")

  "section rules loader" should {
    "load section rules from a csv file" in {
      val maybeRules = SectionRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleSet =>
        ruleSet.rules should have size 4
        ruleSet.headings should have size 3
      }
    }
    "return error if a csv file is not found" in {
      val maybeRules = SectionRulesLoader.load(RulesFileMetaData(3, 2, csvFilePath + "xx", ""))
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return error if a csv file contains invalid data" in {
      val maybeRules = SectionRulesLoader.load(csvMetadataError)
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "provide valid input rules for a matcher against a given fact" in {
      val facts = Facts(Map(
        "Q1" -> >>>("yes"),
        "Q2" -> >>>("no"),
        "Q3" -> >>>("yes")))
      val maybeRules = SectionRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleSet =>
        ruleSet.rules should have size 4
        ruleSet.headings should have size 3
        val response = SectionFactMatcher.matchFacts(facts.facts, ruleSet)
        response.isRight shouldBe true
        response.map { sectionResult =>
          sectionResult.value shouldBe "low"
          sectionResult.exit shouldBe true
        }
      }
    }
  }
}


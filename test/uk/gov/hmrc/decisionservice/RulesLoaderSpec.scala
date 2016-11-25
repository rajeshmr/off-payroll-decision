package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.RulesFileLoadError
import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts, SectionRuleSet}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, FactMatcherInstance, RulesLoaderInstance}
import uk.gov.hmrc.play.test.UnitSpec

class RulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/section_rules_sample.csv"
  val csvFilePathError = "/section_rules_sample_error.csv"
  val csvFileEmpty = "/section_rules_empty.csv"
  val csvFileHeadersOnly = "/section_rules_headers_only.csv"
  val csvFileHeadersError = "/section_rules_headers_error.csv"
  val csvMetadata = RulesFileMetaData(3, csvFilePath, "sectionName")
  val csvMetadataError = RulesFileMetaData(3, csvFilePathError, "sectionName")
  val csvMetadataEmpty = RulesFileMetaData(3, csvFileEmpty, "sectionName")
  val csvMetadataHeadersOnly = RulesFileMetaData(3, csvFileHeadersOnly, "sectionName")
  val csvMetadataHeadersError = RulesFileMetaData(3, csvFileHeadersError, "sectionName")

  "section rules loader" should {
    "load section rules from a csv file" in {
      val maybeRules = RulesLoaderInstance.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleSet =>
        ruleSet.rules should have size 4
        ruleSet.headings should have size 3
      }
    }
    "return error if a csv file is not found" in {
      val maybeRules = RulesLoaderInstance.load(RulesFileMetaData(3, csvFilePath + "xx", ""))
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return error if the csv file contains invalid data" in {
      val maybeRules = RulesLoaderInstance.load(csvMetadataError)
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return error if the csv file is empty" in {
      val maybeRules = RulesLoaderInstance.load(csvMetadataEmpty)
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return no error if the csv file contains only headers" in {
      val maybeRules = RulesLoaderInstance.load(csvMetadataHeadersOnly)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleSet =>
        ruleSet shouldBe a [SectionRuleSet]
        ruleSet.rules should have size (0)
      }
    }
    "return error if the csv file contains incorrect headers" in {
      val maybeRules = RulesLoaderInstance.load(csvMetadataHeadersError)
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
      val maybeRules = RulesLoaderInstance.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleSet =>
        ruleSet.rules should have size 4
        ruleSet.headings should have size 3
        val response = FactMatcherInstance.matchFacts(facts.facts, ruleSet)
        response.isRight shouldBe true
        response.map { sectionResult =>
          sectionResult.value shouldBe "low"
          sectionResult.exit shouldBe true
        }
      }
    }
  }
}


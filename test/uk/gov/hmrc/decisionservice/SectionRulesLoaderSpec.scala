package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{RulesFileLoadError, SectionFact, SectionFacts}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec

class SectionRulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/business_structure.csv"

  "section rules loader" should {
    "load rules from a csv file" in {
      val maybeRules = SectionRulesLoader.load(RulesFileMetaData(List("Q1","Q2","Q3"), List("CO1","CO2"), csvFilePath))
      maybeRules.isRight shouldBe true
      maybeRules.map { rules =>
        rules should have size (4)
      }
    }
    "return error if file is not found" in {
      val maybeRules = SectionRulesLoader.load(RulesFileMetaData(List("Q1","Q2","Q3"), List("CO1","CO2"), csvFilePath + "xx"))
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
      val maybeRules = SectionRulesLoader.load(RulesFileMetaData(List("Q1","Q2","Q3"), List("CO1","CO2"), csvFilePath))
      maybeRules.isRight shouldBe true
      maybeRules.map { rules =>
        rules should have size (4)
        val response = SectionFactMatcher.matchFacts(fact, rules)
        response.isRight shouldBe true
        response.map { sectionResult =>
          sectionResult.value should equal("low")
          sectionResult.exit should equal(true)
        }
      }
    }
  }
}


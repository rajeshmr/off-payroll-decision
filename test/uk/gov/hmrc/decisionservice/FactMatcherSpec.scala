package uk.gov.hmrc.decisionservice

import cats.data.Xor
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine.FactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class FactMatcherSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "fact matcher" should {
    "produce correct result for a sample fact" in {
      val fact = Fact(List(
        FactRow("question1", "yes"),
        FactRow("question2", "no"),
        FactRow("question3", "yes")), "BusinessStructure")
      val rule = Rule(List(
        RuleRow(List("yes","yes","yes"), SectionResult("high"  , true)),
        RuleRow(List("yes","no" ,"no" ), SectionResult("medium", true)),
        RuleRow(List("yes","no" ,"yes"), SectionResult("low"   , true)),
        RuleRow(List("no" ,""   ,"yes"), SectionResult("low"   , false))
      ))

      val response = FactMatcher.matchSectionFact(fact:Fact, rule:Rule)

      response shouldBe a [Xor[DecisionServiceError,SectionResult]]
      response.isRight shouldBe true
      response.map { sectionResult =>
        sectionResult.value should equal("low")
        sectionResult.exit should equal(true)
      }
    }
    "produce error for incorrect (too short) fact" in {
      val fact = Fact(List(
        FactRow("question1", "yes"),
        FactRow("question3", "yes")), "BusinessStructure")
      val rule = Rule(List(
        RuleRow(List("yes","yes","yes"), SectionResult("high"  , true)),
        RuleRow(List("yes","no" ,"no" ), SectionResult("medium", true)),
        RuleRow(List("yes","no" ,"yes"), SectionResult("low"   , true)),
        RuleRow(List("no" ,""   ,"yes"), SectionResult("low"   , false))
      ))

      val response = FactMatcher.matchSectionFact(fact:Fact, rule:Rule)

      response shouldBe a [Xor[DecisionServiceError,SectionResult]]
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce error when match not found" in {
      val fact = Fact(List(
        FactRow("question1", "yes"),
        FactRow("question2", "no"),
        FactRow("question3", "yes")), "BusinessStructure")
      val rule = Rule(List(
        RuleRow(List("yes","yes","yes"), SectionResult("high"  , true)),
        RuleRow(List("yes","no" ,"no" ), SectionResult("medium", true)),
        RuleRow(List("no" ,""   ,"yes"), SectionResult("low"   , false))
      ))

      val response = FactMatcher.matchSectionFact(fact:Fact, rule:Rule)

      response shouldBe a [Xor[DecisionServiceError,SectionResult]]
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
  }

}

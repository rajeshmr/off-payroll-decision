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
      val fact = SectionFacts(List(
        SectionFact("question1", "yes"),
        SectionFact("question2", "no"),
        SectionFact("question3", "yes")), "BusinessStructure")
      val rule = SectionRules(List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), SectionCarryOver("medium", true)),
        SectionRule(List("yes","no" ,"yes"), SectionCarryOver("low"   , true)),
        SectionRule(List("no" ,""   ,"yes"), SectionCarryOver("low"   , false))
      ))

      val response = FactMatcher.matchSectionFacts(fact:SectionFacts, rule:SectionRules)

      response shouldBe a [Xor[DecisionServiceError,SectionCarryOver]]
      response.isRight shouldBe true
      response.map { sectionResult =>
        sectionResult.value should equal("low")
        sectionResult.exit should equal(true)
      }
    }
    "produce error for incorrect (too short) fact" in {
      val fact = SectionFacts(List(
        SectionFact("question1", "yes"),
        SectionFact("question3", "yes")), "BusinessStructure")
      val rule = SectionRules(List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), SectionCarryOver("medium", true)),
        SectionRule(List("yes","no" ,"yes"), SectionCarryOver("low"   , true)),
        SectionRule(List("no" ,""   ,"yes"), SectionCarryOver("low"   , false))
      ))

      val response = FactMatcher.matchSectionFacts(fact:SectionFacts, rule:SectionRules)

      response shouldBe a [Xor[DecisionServiceError,SectionCarryOver]]
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce error when match not found" in {
      val fact = SectionFacts(List(
        SectionFact("question1", "yes"),
        SectionFact("question2", "no"),
        SectionFact("question3", "yes")), "BusinessStructure")
      val rule = SectionRules(List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,""   ,"yes"), SectionCarryOver("low"   , false))
      ))

      val response = FactMatcher.matchSectionFacts(fact:SectionFacts, rule:SectionRules)

      response shouldBe a [Xor[DecisionServiceError,SectionCarryOver]]
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
  }

}

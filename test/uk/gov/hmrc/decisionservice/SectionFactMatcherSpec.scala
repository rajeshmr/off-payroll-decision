package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOverImpl, SectionNotValidUseCase, SectionRule, SectionRuleSet}
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class SectionFactMatcherSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "section fact matcher" should {
    "produce correct result for sample facts" in {
      val fact = Map(
        "question1" -> "yes",
        "question2" -> "no",
        "question3" -> "yes")
      val rules = List(
        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), CarryOverImpl("medium", true)),
        SectionRule(List("yes","no" ,"yes"), CarryOverImpl("low"   , true)),
        SectionRule(List("no" ,""   ,"yes"), CarryOverImpl("low"   , false))
      )
      val ruleSet = SectionRuleSet(List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(fact, ruleSet)
      response.isRight shouldBe true
      response.map { sectionResult =>
        sectionResult.value should equal("low")
        sectionResult.exit should equal(true)
      }
    }
    "produce error for a fact with missing obligatory answers" in {
      val fact = Map(
        "question1" -> "yes",
        "question3" -> "yes")
      val rules = List(
        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), CarryOverImpl("medium", true)),
        SectionRule(List("yes","no" ,"yes"), CarryOverImpl("low"   , true)),
        SectionRule(List("no" ,""   ,"yes"), CarryOverImpl("low"   , false))
      )
      val ruleSet = SectionRuleSet(List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(fact, ruleSet)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce 'section not valid use case' result when match is not found" in {
      val fact = Map(
        "question1" -> "yes",
        "question2" -> "no",
        "question3" -> "yes")
      val rules = List(
        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), CarryOverImpl("medium", true)),
        SectionRule(List("no" ,""   ,"yes"), CarryOverImpl("low"   , false))
      )
      val ruleSet = SectionRuleSet(List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(fact, ruleSet)
      response.isRight shouldBe true
      response.map { r =>
        r shouldBe SectionNotValidUseCase
      }
    }
  }

}

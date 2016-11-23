package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class SectionFactMatcherSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "section fact matcher" should {
    "produce correct result for sample facts" in {
      val facts = Facts(Map(
        "question1" -> >>>("yes"),
        "question2" -> >>>("no"),
        "question3" -> >>>("yes")))
      val rules = List(
        SectionRule(List(>>>("yes"),>>>("yes"),>>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>("no" )), >>>("medium", true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>("yes")), >>>("low"   , true)),
        SectionRule(List(>>>("no" ),>>>(""   ),>>>("yes")), >>>("low"         ))
      )
      val ruleSet = SectionRuleSet("sectionName", List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(facts.facts, ruleSet)
      response.isRight shouldBe true
      response.map { sectionResult =>
        sectionResult.value shouldBe "low"
        sectionResult.exit shouldBe true
      }
    }
    "produce 'section not valid use case' result for a fact with missing obligatory answers" in {
      val facts = Facts(Map(
        "question1" -> >>>("yes"),
        "question3" -> >>>("yes")))
      val rules = List(
        SectionRule(List(>>>("yes"),>>>("yes"),>>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>("no" )), >>>("medium", true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>("yes")), >>>("low"   , true)),
        SectionRule(List(>>>("no" ),>>>(""   ),>>>("yes")), >>>("low"         ))
      )
      val ruleSet = SectionRuleSet("sectionName", List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(facts.facts, ruleSet)
      response.isRight shouldBe true
      response.map { sectionResult =>
        sectionResult shouldBe NotValidUseCase
      }
    }
    "produce 'section not valid use case' result when match is not found" in {
      val facts = Facts(Map(
        "question1" -> >>>("yes"),
        "question2" -> >>>("no"),
        "question3" -> >>>("yes")))
      val rules = List(
        SectionRule(List(>>>("yes"),>>>("yes"),>>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>("no" )), >>>("medium", true)),
        SectionRule(List(>>>("no" ),>>>(""   ),>>>("yes")), >>>("low"         ))
      )
      val ruleSet = SectionRuleSet("sectionName", List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(facts.facts, ruleSet)
      response.isRight shouldBe true
      response.map { r =>
        r shouldBe NotValidUseCase
      }
    }
  }

}

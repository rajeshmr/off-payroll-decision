package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{SectionCarryOver, SectionRule, SectionRuleSet}
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class SectionEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "section fact with empty values matcher" should {
    "produce fact error when fact is missing answers for which there is no match and corresponding rule values are not all empty in any of the rules" in {
      val fact = Map(
        ("question1" -> "yes"),
        ("question2" -> ""),
        ("question3" -> ""))
      val rules = List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,"yes",""   ), SectionCarryOver("low"   , false))
      )
      val ruleSet = SectionRuleSet(List("question1", "question2", "question3"), rules)

      val response = SectionFactMatcher.matchFacts(fact, ruleSet)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce rules error when fact is missing answers for which there is no match but corresponding rule values are empty in at least one rule" in {
      val fact = Map(
        ("question1" -> "yes"),
        ("question2" -> ""),
        ("question3" -> ""))
      val rules = List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,""   ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,""   ,""   ), SectionCarryOver("low"   , false))
      )
      val ruleSet = SectionRuleSet(List("question1", "question2", "question3"), rules)

      val response = SectionFactMatcher.matchFacts(fact, ruleSet)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
  }

}

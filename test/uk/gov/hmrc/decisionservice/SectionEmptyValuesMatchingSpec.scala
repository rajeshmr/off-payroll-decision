package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class SectionEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "section fact matcher" should {
    "produce fact error when fact is missing answers for which there is no match and corresponding rule values are not all empty in any of the rules" in {
      val fact = SectionFacts(List(
        SectionFact("question1", "yes"),
        SectionFact("question2", ""),
        SectionFact("question3", "")), "BusinessStructure")
      val rules = List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,"yes",""   ), SectionCarryOver("low"   , false))
      )

      val response = SectionFactMatcher.matchFacts(fact:SectionFacts, rules)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
    "produce rules error when fact is missing answers for which there is no match but corresponding rule values are empty in at least one rule" in {
      val fact = SectionFacts(List(
        SectionFact("question1", "yes"),
        SectionFact("question2", ""),
        SectionFact("question3", "")), "BusinessStructure")
      val rules = List(
        SectionRule(List("yes","yes","yes"), SectionCarryOver("high"  , true)),
        SectionRule(List("yes","no" ,""   ), SectionCarryOver("medium", true)),
        SectionRule(List("no" ,""   ,""   ), SectionCarryOver("low"   , false))
      )

      val response = SectionFactMatcher.matchFacts(fact:SectionFacts, rules)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
  }

}

package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{CarryOverImpl, SectionNotValidUseCase, SectionRule, SectionRuleSet}
import uk.gov.hmrc.decisionservice.ruleengine.SectionFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class SectionEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val fact = Map("question1" -> "yes", "question2" -> "", "question3" -> "")

  "section fact with empty values matcher" should {
    "produce fact error when there is no match and rule empty values set is does not contain fact empty values set" in {
      val rules = List(
        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
        SectionRule(List("yes","no" ,"no" ), CarryOverImpl("medium", true)),
        SectionRule(List("no" ,"yes",""   ), CarryOverImpl("low"   , false))
      )
      val ruleSet = SectionRuleSet(List("question1", "question2", "question3"), rules)
      val response = SectionFactMatcher.matchFacts(fact, ruleSet)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce 'section not valid use case' result when there is no match and rule empty values set is contains fact empty values set" in {
      val rules = List(
        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
        SectionRule(List("yes","no" ,""   ), CarryOverImpl("medium", true)),
        SectionRule(List("no" ,""   ,""   ), CarryOverImpl("low"   , false))
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

package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules.{SectionRuleSet, _}
import uk.gov.hmrc.decisionservice.ruleengine.FactMatcherInstance
import uk.gov.hmrc.play.test.UnitSpec

class SectionEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val facts = Facts(Map("question1" -> >>>("yes"), "question2" -> >>>(""), "question3" -> >>>("")))

  "section fact with empty values matcher" should {
    "produce fact error when there is no match and rule empty values set is does not contain fact empty values set" in {
      val sectionRules = List(
        SectionRule(List(>>>("yes"),>>>("yes"),>>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>("no" )), >>>("medium", true)),
        SectionRule(List(>>>("no" ),>>>("yes"),>>>(""   )), >>>("low"         ))
      )
      val sectionRuleSet = SectionRuleSet("sectionName",List("question1", "question2", "question3"), sectionRules)
      val response = FactMatcherInstance.matchFacts(facts.facts, sectionRuleSet)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce 'section not valid use case' result when there is no match and rule empty values set is contains fact empty values set" in {
      val sectionRules = List(
        SectionRule(List(>>>("yes"),>>>("yes"),>>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"),>>>("no" ),>>>(""   )), >>>("medium", true)),
        SectionRule(List(>>>("no" ),>>>(""   ),>>>(""   )), >>>("low"         ))
      )
      val sectionRuleSet = SectionRuleSet("sectionName",List("question1", "question2", "question3"), sectionRules)
      val response = FactMatcherInstance.matchFacts(facts.facts, sectionRuleSet)
      response.isRight shouldBe true
      response.map { r =>
        r shouldBe NotValidUseCase
      }
    }
  }

}

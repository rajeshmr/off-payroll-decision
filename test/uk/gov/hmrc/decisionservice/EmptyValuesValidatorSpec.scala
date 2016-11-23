package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine.FactMatcherInstance
import uk.gov.hmrc.play.test.UnitSpec

class EmptyValuesValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val facts = Facts(Map(
    ("question1" -> >>>("yes")),
    ("question2" -> >>>("")),
    ("question3" -> >>>(""))))

  "empty values validator" should {
    "produce fact error if FactsEmptySet is a subset of MaximumRulesEmptySet" in {
      val rules = List(
        SectionRule(List(>>>("yes"), >>>("yes"), >>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"), >>>("no" ), >>>("no" )), >>>("medium", true)),
        SectionRule(List(>>>("no" ), >>>("yes"), >>>(""   )), >>>("low"         ))
      )

      val error = FactMatcherInstance.noMatchResult(facts.facts,rules)
      error.isLeft should be (true)
      error.leftMap { e =>
        e shouldBe a [FactError]
      }
    }
    "produce 'not valid use case' result if FactsEmptySet is a superset of MaximumRulesEmptySet" in {
      val rules = List(
        SectionRule(List(>>>("yes"), >>>("yes"), >>>("yes")), >>>("high"  , true)),
        SectionRule(List(>>>("yes"), >>>("no" ), >>>(""   )), >>>("medium", true)),
        SectionRule(List(>>>("no" ), >>>(""   ), >>>(""   )), >>>("low"         ))
      )

      val result = FactMatcherInstance.noMatchResult(facts.facts,rules)
      result.isRight should be (true)
      result.map { r =>
        r shouldBe NotValidUseCase
      }
    }
  }

}

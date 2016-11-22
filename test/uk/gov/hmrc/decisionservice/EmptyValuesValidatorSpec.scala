package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.ruleengine.EmptyValuesValidator
import uk.gov.hmrc.play.test.UnitSpec

class EmptyValuesValidatorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

//  object SectionEmptyValuesValidator extends EmptyValuesValidator {
//    type ValueType = String
//    type Rule = SectionRule
//    type RuleResult = CarryOver

//    def valueEmpty(s: String) = s.isEmpty
//    def notValidUseCase: CarryOver = SectionNotValidUseCase
//  }

//  "empty values validator" should {
//    "produce fact error if FactsEmptySet is a subset of MaximumRulesEmptySet" in {
//      val fact = Map(
//        ("question1" -> "yes"),
//        ("question2" -> ""),
//        ("question3" -> ""))
//      val rules = List(
//        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
//        SectionRule(List("yes","no" ,"no" ), CarryOverImpl("medium", true)),
//        SectionRule(List("no" ,"yes",""   ), CarryOverImpl("low"   , false))
//      )
//
//      val error = SectionEmptyValuesValidator.noMatchResult(fact,rules)
//      error.isLeft should be (true)
//      error.leftMap { e =>
//        e shouldBe a [FactError]
//      }
//    }
//    "produce 'not valid use case' result if FactsEmptySet is a superset of MaximumRulesEmptySet" in {
//      val fact = Map(
//        ("question1" -> "yes"),
//        ("question2" -> ""),
//        ("question3" -> ""))
//      val rules = List(
//        SectionRule(List("yes","yes","yes"), CarryOverImpl("high"  , true)),
//        SectionRule(List("yes","no" ,""   ), CarryOverImpl("medium", true)),
//        SectionRule(List("no" ,""   ,""   ), CarryOverImpl("low"   , false))
//      )
//
//      val result = SectionEmptyValuesValidator.noMatchResult(fact,rules)
//      result.isRight should be (true)
//      result.map { r =>
//        r shouldBe SectionNotValidUseCase
//      }
//    }
//  }

}

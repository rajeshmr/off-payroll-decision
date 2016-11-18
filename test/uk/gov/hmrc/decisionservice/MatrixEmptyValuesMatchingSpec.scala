package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine.MatrixFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class MatrixEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "matrix fact with empty values matcher" should {
    "produce fact error when fact is missing answers for which rule values are not empty" in {
      val matrixFacts = Map(
        ("BusinessStructure" -> CarryOverImpl("high", false)),
        ("Substitute" -> CarryOverImpl("" , false)),
        ("FinancialRisk" -> CarryOverImpl("" , false))
      )
      val matrixRules = List(
        MatrixRule(List(CarryOverImpl("high"  , false ),CarryOverImpl("high" , false ),CarryOverImpl("low" , false )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("high"  , false ),CarryOverImpl("low" , false),CarryOverImpl("low" , false )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("medium", false ),CarryOverImpl("high", false ),CarryOverImpl("low" , false )), DecisionOutOfIR35)
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute", "FinancialRisk"), matrixRules)

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRuleSet)
      println(response)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [FactError]
      }
    }
    "produce 'not valid use case' result when fact is missing answers for which there is no match but corresponding rule values are empty in at least one rule" in {
      val matrixFacts = Map(
        ("BusinessStructure" -> CarryOverImpl("high", false)),
        ("Substitute" -> CarryOverImpl("" , false)),
        ("FinancialRisk" -> CarryOverImpl("" , false))
      )
      val matrixRules = List(
        MatrixRule(List(CarryOverImpl("high"  , false ),CarryOverImpl("high" , false ),CarryOverImpl("low" , false )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("high"  , false ),CarryOverImpl("low" , false),CarryOverImpl("low" , false )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("medium", false ),CarryOverImpl("", false ),CarryOverImpl("" , false )), DecisionOutOfIR35)
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute", "FinancialRisk"), matrixRules)

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRuleSet)
      println(response)
      response.isRight shouldBe true
      response.map { r =>
        r shouldBe a [MatrixDecision]
        r.value shouldBe "NotValidUseCase"
      }
    }
  }

}

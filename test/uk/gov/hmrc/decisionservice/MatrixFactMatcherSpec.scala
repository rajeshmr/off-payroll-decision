package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.rules.{DecisionInIR35, _}
import uk.gov.hmrc.decisionservice.ruleengine.MatrixFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class MatrixFactMatcherSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "matrix fact matcher" should {
    "produce correct result for a sample matrix fact" in {
      val matrixFacts = Map(
        "BusinessStructure" -> CarryOverImpl("high", true), "Substitute" -> CarryOverImpl("high" , false)
      )
      val matrixRules = List(
        MatrixRule(List(CarryOverImpl("high"  , true ),CarryOverImpl("low" , true )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("high"  , true ),CarryOverImpl("high", false)), DecisionOutOfIR35),
        MatrixRule(List(CarryOverImpl("medium", true ),CarryOverImpl("high", true )), DecisionInIR35)
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute"), matrixRules)

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRuleSet)

      response.isRight shouldBe true
      response.map { decision =>
        decision shouldBe DecisionOutOfIR35
      }
    }
    "produce correct result for a partial fact" in {
      val matrixFacts = Map(
        "BusinessStructure" -> CarryOverImpl("high", true),
        "Substitute" -> CarryOverImpl("low" , false),
        "FinancialRisk" -> CarryOverImpl("" , false)
      )
      val matrixRules = List(
        MatrixRule(List(CarryOverImpl("high"  , true ),CarryOverImpl("high" , true ),CarryOverImpl("" , true )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("high"  , true ),CarryOverImpl("low" , false),CarryOverImpl("" , true )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("medium", true ),CarryOverImpl("high", true ),CarryOverImpl("low" , true )), DecisionOutOfIR35)
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute", "FinancialRisk"), matrixRules)

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRuleSet)
      response.isRight shouldBe true
      response.map { decision =>
        decision shouldBe DecisionInIR35
      }
    }
  }

}

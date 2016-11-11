package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{SectionCarryOver, _}
import uk.gov.hmrc.decisionservice.ruleengine.MatrixFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class MatrixFactMatcherSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "matrix fact matcher" should {
    "produce correct result for a sample matrix fact" in {
      val matrixFacts = MatrixFacts(List(
        MatrixFact("BusinessStructure", SectionCarryOver("high", true)), MatrixFact("Substitute", SectionCarryOver("high" , false))
      ))
      val matrixRules = List(
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("low" , true )), MatrixDecision("in IR35")),
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("high", false)), MatrixDecision("out of IR35")),
        MatrixRule(List(SectionCarryOver("medium", true ),SectionCarryOver("high", true )), MatrixDecision("in IR35"))
      )

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRules)

      response.isRight shouldBe true
      response.map { decision =>
        decision.value should equal("out of IR35")
      }
    }
    "produce correct result for a partial fact" in {
      val matrixFacts = MatrixFacts(List(
        MatrixFact("BusinessStructure", SectionCarryOver("high", true)),
        MatrixFact("Substitute", SectionCarryOver("low" , false)),
        MatrixFact("FinancialRisk", SectionCarryOver("" , false))
      ))
      val matrixRules = List(
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("high" , true ),SectionCarryOver("" , true )), MatrixDecision("self employed")),
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("low" , false),SectionCarryOver("" , true )), MatrixDecision("in IR35")),
        MatrixRule(List(SectionCarryOver("medium", true ),SectionCarryOver("high", true ),SectionCarryOver("low" , true )), MatrixDecision("out of IR35"))
      )

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRules)

      response.isRight shouldBe true
      response.map { decision =>
        decision.value should equal("in IR35")
      }
    }
  }

}

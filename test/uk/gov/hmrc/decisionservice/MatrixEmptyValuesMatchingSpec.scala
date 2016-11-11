package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{SectionCarryOver, _}
import uk.gov.hmrc.decisionservice.ruleengine.MatrixFactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class MatrixEmptyValuesMatchingSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "matrix fact matcher" should {
    "produce error when fact is missing answers for which rule values are not empty" in {
      val matrixFacts = MatrixFacts(List(
        MatrixFact("BusinessStructure", SectionCarryOver("high", true)),
        MatrixFact("Substitute", SectionCarryOver("" , false)),
        MatrixFact("FinancialRisk", SectionCarryOver("" , false))
      ))
      val matrixRules = List(
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("high" , true ),SectionCarryOver("low" , true )), MatrixDecision("self employed")),
        MatrixRule(List(SectionCarryOver("high"  , true ),SectionCarryOver("low" , false),SectionCarryOver("low" , true )), MatrixDecision("in IR35")),
        MatrixRule(List(SectionCarryOver("medium", true ),SectionCarryOver("high", true ),SectionCarryOver("low" , true )), MatrixDecision("out of IR35"))
      )

      val response = MatrixFactMatcher.matchFacts(matrixFacts, matrixRules)
      println(response)
      response.isLeft shouldBe true
      response.leftMap { error =>
        error shouldBe a [RulesFileError]
      }
    }
  }

}

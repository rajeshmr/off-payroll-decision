package uk.gov.hmrc.decisionservice

import cats.data.Xor
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, Fact, Rule, SectionResult}
import uk.gov.hmrc.decisionservice.ruleengine.FactMatcher
import uk.gov.hmrc.play.test.UnitSpec

class FactMatcherSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "fact matcher" should {
    "produce correct result for a sample fact" in {

      val fact = Fact(List(("question1", "yes")), "BusinessStructure")

      val rule = Rule(List((List("yes"), SectionResult("high", true))))

      val response = FactMatcher.matchSectionFact(fact:Fact, rule:Rule)

      response shouldBe a [Xor[DecisionServiceError,SectionResult]]

    }
  }

}

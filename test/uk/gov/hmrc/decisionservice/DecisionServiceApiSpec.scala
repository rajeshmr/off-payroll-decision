package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.decisionservice.model.rules.{DecisionInIR35, _}
import uk.gov.hmrc.decisionservice.ruleengine.MatrixFactMatcher
import uk.gov.hmrc.decisionservice.service.DecisionService
import uk.gov.hmrc.play.test.UnitSpec

class DecisionServiceApiSpec extends UnitSpec {

  "decision service" should {
    "produce decision given question set" in {
      val version = "1.0"
      val questionSet =
        QuestionSet( version,
          Map("BusinessStructure" -> Map("q1-tag" -> "Yes",
                                         "q2-tag" -> "No"),
              "PersonalService"   -> Map("q3-tag" -> "No")
          )
        )
      val matrixRules = List(
        MatrixRule(List(CarryOverImpl("high"  , true ),CarryOverImpl("low" , true )), DecisionInIR35),
        MatrixRule(List(CarryOverImpl("high"  , true ),CarryOverImpl("high", false)), DecisionOutOfIR35),
        MatrixRule(List(CarryOverImpl("medium", true ),CarryOverImpl("high", true )), DecisionInIR35)
      )
      val matrixRuleSet = MatrixRuleSet(List("BusinessStructure", "Substitute"), matrixRules)

//      val result = DecisionService.evaluate(questionSet, matrixRuleSet);
//
//      result.isRight shouldBe true
//      result.map { decision =>
//        decision shouldBe DecisionOutOfIR35
//      }
    }
  }

}


// case class QuestionSet(version:String, sections:Map[String,Map[String,Boolean]])

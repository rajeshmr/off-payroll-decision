package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.{BusinessStructure, CarryOver, Decision, Substitution}
import uk.gov.hmrc.decisionservice.service.RulesExecutor
import uk.gov.hmrc.play.test.UnitSpec

import scala.collection.JavaConversions._

class RulesExecutorSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "processing rules" should {
    "return expected facts" in {
      val model = List(
        Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes")),
        BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))
      )
      val found = RulesExecutor.analyze(model, "rules-test-basic.xls")
      val carryOvers = found collect { case x:CarryOver => x}
      val decisions = found collect { case x:Decision => x }
      found should have size(5)
      carryOvers should have size(2)
      decisions should have size(1)
      (carryOvers map { _.value }).toList should contain theSameElementsAs List("high", "out")
      (decisions map {_.text}).head should equal ("out of IR35")
    }
    "treat section empty inputs as always matching" in {
      val model = List(
        BusinessStructure(Map("advertiseForWork" -> "no"))
      )
      val found = RulesExecutor.analyze(model, "rules-test-emptyvalues-section.xls")
      val carryOvers = found collect { case x:CarryOver => x}
      found should have size(2)
      carryOvers should have size(1)
      (carryOvers map { _.value }).head should equal ("low")
    }
    "treat decision empty inputs as always matching" in {
      val model = List(
        Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes")),
        BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))
      )
      val found = RulesExecutor.analyze(model, "rules-test-emptyvalues-decision.xls")
      val carryOvers = found collect { case x:CarryOver => x}
      val decisions = found collect { case x:Decision => x }
      found should have size(5)
      carryOvers should have size(2)
      decisions should have size(1)
      (decisions map {_.text}).head should equal ("out")
    }
  }

}

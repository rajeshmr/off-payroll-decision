package uk.gov.hmrc.decisionservice

import org.junit.runner.RunWith
import org.scalatest.{FunSuite, ShouldMatchers}
import org.scalatest.junit.JUnitRunner
import uk.gov.hmrc.decisionservice.model.{BusinessStructure, CarryOver, Decision, Substitution}
import uk.gov.hmrc.decisionservice.service.RulesExecutor

import collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class RulesExecutorSpec extends FunSuite with ShouldMatchers {

  def model1 = {
    val substitution = Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes"))
    val businessStructure = BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))

    List(
      substitution,
      businessStructure
    )
  }

  test("basic testcase") {
    val found = RulesExecutor.analyze(model1, "sheets/kb-rules-01.xls")
    val carryOvers = found collect { case x:CarryOver => x}
    val decisions = found collect { case x:Decision => x }
    found should have size(5)
    carryOvers should have size(2)
    decisions should have size(1)
    carryOvers.foreach{println(_)}
    decisions.foreach{println(_)}
  }


}

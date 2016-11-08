package uk.gov.hmrc.decisionservice

import java.lang.System._
import java.util.concurrent.TimeUnit.NANOSECONDS

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.service.RulesExecutor
import uk.gov.hmrc.play.test.UnitSpec

class RulesExecutorPerformanceSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  "processing rules" should {
    "return expected facts" in {
      val model = List(
        Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes")),
        BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))
      )
      val start = nanoTime
      val futures = (1 to 200) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls")
      }
      futures.foreach(await(_))
      val duration = nanoTime - start
      println("completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
  }

}

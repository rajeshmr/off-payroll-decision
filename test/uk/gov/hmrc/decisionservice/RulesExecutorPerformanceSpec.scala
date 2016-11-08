package uk.gov.hmrc.decisionservice

import java.lang.System._
import java.util.concurrent.TimeUnit.NANOSECONDS

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.service.RulesExecutor
import uk.gov.hmrc.play.test.UnitSpec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


import scala.concurrent.Future

class RulesExecutorPerformanceSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val model = List(
    Substitution(Map("rightToSendSubstituteInContract" -> "yes", "obligationToSendSubstituteInContract" -> "yes")),
    BusinessStructure(Map("advertiseForWork" -> "yes", "expenseRunningBusinessPremises" -> "yes"))
  )

  implicit val timeout = 20 seconds

  "processing rules in parallel" should {
    "return within a specified time limit" in {
      val start = nanoTime
      val futures = (1 to 200) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls")
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
  }

  "processing rules sequentially" should {
    "return within a specified time limit" in {
      val start = nanoTime
      val futures = (1 to 200) map { a =>
        await(RulesExecutor.analyze(model, "rules-test-basic.xls"))
      }
      val duration = nanoTime - start
      println("sequential execution completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
  }

}

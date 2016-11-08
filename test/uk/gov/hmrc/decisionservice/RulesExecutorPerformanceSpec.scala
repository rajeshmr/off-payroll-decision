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
        Future { RulesExecutor.analyze(model, "rules-test-basic.xls") }
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
  }

  "processing rules in parallel with pre-initialized knowledge base" should {
    "return within a smaller time than when initializing knowledge base at each call" in {
      val start = nanoTime
      val kb = RulesExecutor.createKb("rules-test-emptyvalues-section.xls")
      val futures = (1 to 200) map { a =>
        Future { RulesExecutor.analyze(model, "rules-test-emptyvalues-section.xls", kb) }
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution with pre-initialized kb completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
    "return within a small time for two different knowledge bases" in {
      val start = nanoTime
      val kb1 = RulesExecutor.createKb("rules-test-basic.xls")
      val kb2 = RulesExecutor.createKb("rules-test-emptyvalues-section.xls")
      val futures = (1 to 200) map { a =>
        Future { RulesExecutor.analyze(model, "abc", if (a % 2 == 0) kb1 else kb2) }
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution with two pre-initialized knowledge bases completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }

  }

  "processing rules sequentially" should {
    "return within a specified time limit" in {
      val start = nanoTime
      (1 to 200) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls")
      }
      val duration = nanoTime - start
      println("sequential execution completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
  }

  "processing rules sequentially with pre-initialized knowledge base" should {
    "return within a smaller time than when initializing knowledge base at each call" in {
      val start = nanoTime
      val kb = RulesExecutor.createKb("rules-test-basic.xls")
      (1 to 200) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls", kb)
      }
      val duration = nanoTime - start
      println("sequential execution with pre-initialized kb completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
    "return within a small time for a large number of iterations" in {
      val start = nanoTime
      val kb = RulesExecutor.createKb("rules-test-basic.xls")
      (1 to 10000) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls", kb)
      }
      val duration = nanoTime - start
      println("sequential execution with pre-initialized kb completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
    "return within a small time for two different knowledge bases" in {
      val start = nanoTime
      val kb1 = RulesExecutor.createKb("rules-test-basic.xls")
      val kb2 = RulesExecutor.createKb("rules-test-emptyvalues-section.xls")
      (1 to 10000) map { a =>
        RulesExecutor.analyze(model, "abc", if (a % 2 == 0) kb1 else kb2)
      }
      val duration = nanoTime - start
      println("sequential execution with two pre-initialized knowledge bases completed in %,d ms.".format(NANOSECONDS.toMillis(duration)))
    }
  }

}

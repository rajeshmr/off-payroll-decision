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
      val Iterations = 100
      val futures = (1 to Iterations) map { a =>
        Future { RulesExecutor.analyze(model, "rules-test-basic.xls") }
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution of %d iterations, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }
  }

  "processing rules in parallel with pre-initialized knowledge base" should {
    "return within a smaller time than when initializing knowledge base at each call" in {
      val start = nanoTime
      val Iterations = 5000
      val kb = RulesExecutor.createKb("rules-test-emptyvalues-section.xls")
      val futures = (1 to Iterations) map { a =>
        Future { RulesExecutor.analyze(model, "rules-test-emptyvalues-section.xls", kb) }
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution of %d iterations, with pre-initialized kb, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }
    "return within a small time for two different knowledge bases" in {
      val start = nanoTime
      val Iterations = 10000
      val kb1 = RulesExecutor.createKb("rules-test-basic.xls")
      val kb2 = RulesExecutor.createKb("rules-test-emptyvalues-section.xls")
      val futures = (1 to Iterations) map { a =>
        Future { RulesExecutor.analyze(model, "abc", if (a % 2 == 0) kb1 else kb2) }
      }
      await(Future.sequence(futures))
      val duration = nanoTime - start
      println("parallel execution of %d iterations, with two pre-initialized kbs, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }

  }

  "processing rules sequentially" should {
    "return within a specified time limit" in {
      val start = nanoTime
      val Iterations = 100
      (1 to Iterations) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls")
      }
      val duration = nanoTime - start
      println("sequential execution of %d iterations, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }
  }

  "processing rules sequentially with pre-initialized knowledge base" should {
    "return within a smaller time than when initializing knowledge base at each call" in {
      val start = nanoTime
      val Iterations = 100
      val kb = RulesExecutor.createKb("rules-test-basic.xls")
      (1 to Iterations) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls", kb)
      }
      val duration = nanoTime - start
      println("sequential execution of %d iterations, with pre-initialized kb, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }
    "return within a small time for a large number of iterations" in {
      val start = nanoTime
      val Iterations = 10000
      val kb = RulesExecutor.createKb("rules-test-basic.xls")
      (1 to Iterations) map { a =>
        RulesExecutor.analyze(model, "rules-test-basic.xls", kb)
      }
      val duration = nanoTime - start
      println("sequential execution of %d iterations, with pre-initialized kb, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }
    "return within a small time for two different knowledge bases" in {
      val start = nanoTime
      val Iterations = 10000
      val kb1 = RulesExecutor.createKb("rules-test-basic.xls")
      val kb2 = RulesExecutor.createKb("rules-test-emptyvalues-section.xls")
      (1 to Iterations) map { a =>
        RulesExecutor.analyze(model, "abc", if (a % 2 == 0) kb1 else kb2)
      }
      val duration = nanoTime - start
      println("sequential execution of %d iterations, with two pre-initialized kbs, completed in %,d ms".format(Iterations, NANOSECONDS.toMillis(duration)))
    }
  }

}

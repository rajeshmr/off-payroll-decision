package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import org.drools.KnowledgeBase
import org.drools.builder.{KnowledgeBuilderFactory, ResourceType}
import org.drools.io.ResourceFactory
import org.slf4j.LoggerFactory
import play.api.i18n.Messages
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, KnowledgeBaseError, RulesFileError}

import scala.collection.JavaConversions._


object RulesExecutor {
  val logger = LoggerFactory.getLogger(RulesExecutor.getClass())
  val LoggerVariable: String = "logger"
  val DroolsDialect: String = "JANINO"
  val DroolsDialectMvelStrict: String = "false"

  def using[R, T <: { def dispose() }](getres: => T)(doit: T => R): R = {
    val res = getres
    try doit(res) finally res.dispose
  }

  def analyze(model: List[Any], kb: String):Xor[DecisionServiceError,List[AnyRef]] = {
    analyze(model, kb, createKb(kb))
  }

  def analyze(model: List[Any], kb: String, maybeKnowledgeBase:Xor[DecisionServiceError,KnowledgeBase]):Xor[DecisionServiceError,List[AnyRef]] = {
    maybeKnowledgeBase match {
      case Xor.Right(knowledgeBase) =>
        val results = using(knowledgeBase.newStatefulKnowledgeSession()) { session =>
          session.setGlobal(LoggerVariable, LoggerFactory.getLogger(kb))
          model.foreach(session.insert(_))
          session.fireAllRules()
          session.getObjects()
        }
        Xor.right(results.toList)
      case e@Xor.Left(ee) => e
    }
  }

  def createKb(kb: String): Xor[DecisionServiceError,KnowledgeBase] = {
    try {
      System.setProperty("drools.dialect.java.compiler", DroolsDialect)
      val config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration()
      config.setProperty("drools.dialect.mvel.strict", DroolsDialectMvelStrict)
      val res = ResourceFactory.newClassPathResource(kb)
      val knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config)
      knowledgeBuilder.add(res, ResourceType.DTABLE)
      val errors = knowledgeBuilder.getErrors()
      errors.size() match {
        case n if n > 0 =>
          for (error <- errors) logger.error(error.getMessage())
          Xor.left(KnowledgeBaseError(Messages("rules.executor.knowledge.base.error") + s" $errors"))
        case _ =>
          Xor.right(knowledgeBuilder.newKnowledgeBase())
      }
    } catch {
      case e:Throwable => Xor.left(RulesFileError(e.getMessage))
    }
  }
}

package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import org.drools.builder.{KnowledgeBuilderFactory, ResourceType}
import org.drools.io.ResourceFactory
import org.slf4j.LoggerFactory
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, KnowledgeBaseError, RulesFileError}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

object RulesExecutor {
  val logger = LoggerFactory.getLogger(RulesExecutor.getClass())

  def using[R, T <% { def dispose() }](getres: => T)(doit: T => R): R = {
    val res = getres
    try doit(res) finally res.dispose
  }

  def analyze(model: List[Any], kb: String):Xor[DecisionServiceError,List[AnyRef]] = {

    val tryBuilder = Try {
      System.setProperty("drools.dialect.java.compiler", "JANINO")
      val config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration()
      config.setProperty("drools.dialect.mvel.strict", "false")
      val res = ResourceFactory.newClassPathResource(kb)
      val knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config)
      knowledgeBuilder.add(res, ResourceType.DTABLE)
      knowledgeBuilder
    }
    tryBuilder match {
      case Success(builder) =>
        val errors = builder.getErrors()
        errors.size() match {
          case n if n > 0 =>
            for (error <- errors) logger.error(error.getMessage())
            Xor.left(KnowledgeBaseError(s"Problem(s) with knowledge base $errors"))
          case _ =>
            val kbase = builder.newKnowledgeBase()
            val results = using(kbase.newStatefulKnowledgeSession()) { session =>
              session.setGlobal("logger", LoggerFactory.getLogger(kb))
              model.foreach(session.insert(_))
              session.fireAllRules()
              session.getObjects()
            }
            Xor.right(results.toList)
        }
      case Failure(e) =>
        Xor.left(RulesFileError(e.getMessage))
    }
  }

}

package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import org.drools.builder.{KnowledgeBuilder, KnowledgeBuilderFactory, ResourceType}
import org.drools.io.ResourceFactory
import org.slf4j.LoggerFactory
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, KnowledgeBaseError, RulesFileError}

import scala.collection.JavaConversions._
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


object RulesExecutor {
  val logger = LoggerFactory.getLogger(RulesExecutor.getClass())

  def using[R, T <% { def dispose() }](getres: => T)(doit: T => R): R = {
    val res = getres
    try doit(res) finally res.dispose
  }

  def analyze(model: List[Any], kb: String):Future[Xor[DecisionServiceError,List[AnyRef]]] = {
    val tryBuilder = createKnowledgeBuilder(kb)
    tryBuilder.flatMap {
      case Xor.Right(builder) =>
        val errors = builder.getErrors()
        errors.size() match {
          case n if n > 0 =>
            for (error <- errors) logger.error(error.getMessage())
            Future.successful(Xor.left(KnowledgeBaseError(s"Problem(s) with knowledge base $errors")))
          case _ =>
            Future {
              val kbase = builder.newKnowledgeBase()
              val results = using(kbase.newStatefulKnowledgeSession()) { session =>
                session.setGlobal("logger", LoggerFactory.getLogger(kb))
                model.foreach(session.insert(_))
                session.fireAllRules()
                session.getObjects()
              }
              Xor.right(results.toList)
            }
        }
      case e@Xor.Left(ee) => Future.successful(e)
    }
  }

  def createKnowledgeBuilder(kb: String): Future[Xor[DecisionServiceError,KnowledgeBuilder]] = {
    Future {
      System.setProperty("drools.dialect.java.compiler", "JANINO")
      val config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration()
      config.setProperty("drools.dialect.mvel.strict", "false")
      val res = ResourceFactory.newClassPathResource(kb)
      val knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config)
      knowledgeBuilder.add(res, ResourceType.DTABLE)
      Xor.right(knowledgeBuilder)
    }.recover {
      case e => Xor.left(RulesFileError(e.getMessage))
    }
  }
}

package uk.gov.hmrc.decisionservice.service

import java.io.File
import java.util

import org.drools.builder.{KnowledgeBuilderFactory, ResourceType}
import org.drools.impl.adapters.AgendaAdapter
import org.drools.io.ResourceFactory
import org.slf4j.LoggerFactory

import collection.JavaConversions._

object RulesExecutor {
  val logger = LoggerFactory.getLogger(RulesExecutor.getClass())

  def using[R, T <% { def dispose() }](getres: => T)(doit: T => R): R = {
    val res = getres
    try doit(res) finally res.dispose
  }

  def analyze(model: List[Any], kb: String):util.Collection[AnyRef] = {
    System.setProperty("drools.dialect.java.compiler", "JANINO")

    val config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration()
    config.setProperty("drools.dialect.mvel.strict", "false")
    val kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config)

//    val res = ResourceFactory.newClassPathResource(kb)
    val res = ResourceFactory.newFileResource(new File(kb))
//    kbuilder.add(res, ResourceType.DRL)
    kbuilder.add(res, ResourceType.DTABLE)

    val errors = kbuilder.getErrors();
    if (errors.size() > 0) {
      for (error <- errors) logger.error(error.getMessage())
      throw new IllegalArgumentException("Problem with the Knowledge base");
    }


    val kbase = kbuilder.newKnowledgeBase()

    val results = using(kbase.newStatefulKnowledgeSession()) { session =>
      session.setGlobal("logger", LoggerFactory.getLogger(kb))
      model.foreach(session.insert(_))
      val agenda = session.getAgenda.asInstanceOf[AgendaAdapter]
      session.fireAllRules()
      session.getObjects()
    }

    results
  }
}

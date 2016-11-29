/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  def using[R <: { def dispose(): Unit }, B](resource: R)(f: R => B): B = try { f(resource) } finally { resource.dispose() }

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

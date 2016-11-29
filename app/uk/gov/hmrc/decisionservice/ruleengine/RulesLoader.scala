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

package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.model.{_}

import scala.io.Source
import scala.util.{Failure, Success, Try}

case class RulesFileMetaData(valueCols:Int, resultCols:Int, path:String){
  def numCols = valueCols + resultCols
}


trait RulesLoader {
  type ValueType
  type Rule
  type RuleSet

  val Separator = ','

  def using[R <: { def close(): Unit }, B](resource: R)(f: R => B): B = try { f(resource) } finally { resource.close() }

  def load(rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,RuleSet] = {
      Try {
        val is = getClass.getResourceAsStream(rulesFileMetaData.path)
        using(Source.fromInputStream(is)) { res =>
          val tokens = res.getLines.map(_.split(Separator).map(_.trim).toList).toList
          val (headings::rest) = tokens
          val rules = (for (lineTokens <- rest) yield {
            lineTokens match {
              case t if isValidRule(t, rulesFileMetaData) => createRule(t, rulesFileMetaData)
            }
          })
          createRuleSet(rules, headings)
        }
      } match {
        case Success(content) => Xor.right(content)
        case Failure(e) => Xor.left(RulesFileLoadError(e.getMessage))
      }
    }

  def isValidRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):Boolean = {
    tokens.size == rulesFileMetaData.numCols
  }

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):Rule

  def createRuleSet(rules:List[Rule], headings:List[String]):RuleSet
}


object SectionRulesLoader extends RulesLoader {
  type ValueType = String
  type Rule = SectionRule
  type RuleSet = SectionRuleSet

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):SectionRule = {
    val result = SectionCarryOver(tokens.drop(rulesFileMetaData.valueCols).head, tokens.last.toBoolean)
    val values = tokens.take(rulesFileMetaData.valueCols)
    SectionRule(values, result)
  }

  def createRuleSet(rules:List[SectionRule], headings:List[String]):SectionRuleSet = {
    SectionRuleSet(headings, rules)
  }
}


object MatrixRulesLoader extends RulesLoader {
  type ValueType = SectionCarryOver
  type Rule = MatrixRule
  type RuleSet = MatrixRuleSet

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):MatrixRule = {
    val result = MatrixDecision(tokens.last)
    val values = tokens.take(tokens.size-1).map(SectionCarryOver(_,false))
    MatrixRule(values, result)
  }

  def createRuleSet(rules:List[MatrixRule], headings:List[String]):MatrixRuleSet = {
    MatrixRuleSet(headings, rules)
  }
}

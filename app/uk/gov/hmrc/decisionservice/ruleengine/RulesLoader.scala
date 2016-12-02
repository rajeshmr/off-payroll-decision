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
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._

import scala.util.{Failure, Success, Try}
import RulesFileReaderTokenizer._
import uk.gov.hmrc.decisionservice.model.api.ErrorCodes._

trait RulesLoader {

  val rulesFileLineValidator:RulesFileLineValidator

  def load(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileError,SectionRuleSet] =
    tokenize match {
      case Success(tokens) =>
        parseRules(tokens)
      case Failure(e) =>
        Xor.left(RulesFileError(UNABLE_TO_READ_OR_PARSE_RULES_FILE, e.getMessage))
    }

  private def parseRules(tokens: List[List[String]])(implicit rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, SectionRuleSet] = tokens match {
    case Nil => Xor.left(RulesFileError(EMPTY_RULES_FILE, s"empty rules file ${rulesFileMetaData.path}"))
    case (headings :: rest) =>
      val errorsInHeadings = rulesFileLineValidator.validateColumnHeaders(headings, rulesFileMetaData).swap.toList
      val errorsInRules = rest.zipWithIndex.map(validateLine _).collect { case Xor.Left(e) => e }
      errorsInRules ::: errorsInHeadings match {
        case Nil => createRuleSet(rulesFileMetaData, rest, headings)
        case l => Xor.left(errorsInRules.foldLeft(RulesFileError(0, ""))(_ ++ _))
      }
  }

  private def validateLine(tokensWithIndex:(List[String],Int))(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileError,Unit] = {
    tokensWithIndex match {
      case (t, l) if t.slice(rulesFileMetaData.valueCols, rulesFileMetaData.numCols).isEmpty =>
        Xor.left(RulesFileError(RESULT_MISSING_IN_RULES_FILE, s"in line ${l+2} all result tokens are empty in file ${rulesFileMetaData.path}"))
      case (t, l) if t.size > rulesFileMetaData.numCols =>
        Xor.left(RulesFileError(TOO_MANY_COLUMNS_IN_RULES_FILE, s"in line ${l+2} number of columns is ${t.size}, should be no more than ${rulesFileMetaData.numCols} in file ${rulesFileMetaData.path}"))
      case (t, l) =>
        rulesFileLineValidator.validateLine(t, rulesFileMetaData, l+2)
      case _ =>
        Xor.right(())
    }
  }

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):SectionRule = {
    val result = >>>(tokens.drop(rulesFileMetaData.valueCols))
    val values = tokens.take(rulesFileMetaData.valueCols)
    SectionRule(values.map(>>>(_)), result)
  }

  def createRuleSet(rulesFileMetaData:RulesFileMetaData, ruleTokens:List[List[String]], headings:List[String]):Xor[RulesFileError,SectionRuleSet] = {
    Try {
      val rules = ruleTokens.map(createRule(_, rulesFileMetaData))
      SectionRuleSet(rulesFileMetaData.name, headings.take(rulesFileMetaData.valueCols), rules)
    }
    match {
      case Success(sectionRuleSet) => Xor.right(sectionRuleSet)
      case Failure(e) => Xor.left(RulesFileError(UNABLE_TO_CREATE_RULE, e.getMessage))
    }
  }

  def createErrorMessage(tokens:List[List[String]]):String = tokens.map(a => s"$a").mkString(" ")
}

object RulesLoaderInstance extends RulesLoader {
  val rulesFileLineValidator = RulesFileLineValidatorInstance
}

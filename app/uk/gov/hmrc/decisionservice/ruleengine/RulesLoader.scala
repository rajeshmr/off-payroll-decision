package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._
import scala.util.{Failure, Success, Try}
import RulesFileReaderTokenizer._

case class RulesFileMetaData(valueCols:Int, path:String, name:String){
  val ResultColumns = 3
  def numCols = valueCols + ResultColumns
}


trait RulesLoader {

  val rulesFileLineValidator:RulesFileLineValidator

  def load(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,SectionRuleSet] =
    tokenize match {
      case Success(tokens) =>
        parseRules(tokens)
      case Failure(e) =>
        Xor.left(RulesFileLoadError(e.getMessage))
    }

  private def parseRules(tokens: List[List[String]])(implicit rulesFileMetaData: RulesFileMetaData): Xor[RulesFileLoadError, SectionRuleSet] = tokens match {
    case Nil => Xor.left(RulesFileLoadError(s"empty rules file ${rulesFileMetaData.path}"))
    case (headings :: rest) =>
      val errorsInHeadings = rulesFileLineValidator.validateColumnHeaders(headings, rulesFileMetaData).swap.toList
      val errorsInRules = rest.zipWithIndex.map(validateLine _).collect { case Xor.Left(e) => e }
      errorsInRules ::: errorsInHeadings match {
        case Nil => createRuleSet(rulesFileMetaData, rest, headings)
        case l => Xor.left(errorsInRules.foldLeft(RulesFileLoadError(""))(_ ++ _))
      }
  }

  private def validateLine(tokensWithIndex:(List[String],Int))(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,Unit] = {
    tokensWithIndex match {
      case (t, l) if t.slice(rulesFileMetaData.valueCols, rulesFileMetaData.numCols).isEmpty =>
        Xor.left(RulesFileLoadError(s"in line ${l+2} all result tokens are empty in file ${rulesFileMetaData.path}"))
      case (t, l) if t.size > rulesFileMetaData.numCols =>
        Xor.left(RulesFileLoadError(s"in line ${l+2} number of columns is ${t.size}, should be no more than ${rulesFileMetaData.numCols} in file ${rulesFileMetaData.path}"))
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

  def createRuleSet(rulesFileMetaData:RulesFileMetaData, ruleTokens:List[List[String]], headings:List[String]):Xor[RulesFileLoadError,SectionRuleSet] = {
    Try {
      val rules = ruleTokens.map(createRule(_, rulesFileMetaData))
      SectionRuleSet(rulesFileMetaData.name, headings.take(rulesFileMetaData.valueCols), rules)
    }
    match {
      case Success(sectionRuleSet) => Xor.right(sectionRuleSet)
      case Failure(e) => Xor.left(RulesFileLoadError(e.getMessage))
    }
  }

  def createErrorMessage(tokens:List[List[String]]):String = tokens.map(a => s"$a").mkString(" ")
}

object RulesLoaderInstance extends RulesLoader {
  val rulesFileLineValidator = RulesFileLineValidatorInstance
}

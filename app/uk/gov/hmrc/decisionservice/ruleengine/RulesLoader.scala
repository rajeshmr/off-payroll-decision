package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._
import uk.gov.hmrc.decisionservice.model.rules._
import scala.util.{Failure, Success, Try}
import RulesFileReaderTokenizer._

case class RulesFileMetaData(valueCols:Int, resultCols:Int, path:String, name:String){
  def numCols = valueCols + resultCols
}


trait RulesLoader {

  def load(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,SectionRuleSet]

}

object SectionRulesLoader extends RulesLoader {

  def load(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,SectionRuleSet] =
    tokenize match {
      case Success(tokens) =>
        parseRules(tokens)
      case Failure(e) =>
        Xor.left(RulesFileLoadError(e.getMessage))
    }

  private def parseRules(tokens: List[List[String]])(implicit rulesFileMetaData: RulesFileMetaData): Xor[RulesFileLoadError, SectionRuleSet] = {
    val (headings :: rest) = tokens
    val errorRuleTokens = rest.zipWithIndex.map(validateLine _).collect { case Xor.Left(e) => e }
    errorRuleTokens match {
      case Nil => createRuleSet(rulesFileMetaData, rest, headings)
      case l => Xor.left(errorRuleTokens.foldLeft(RulesFileLoadError(""))(_ ++ _))
    }
  }

  private def validateLine(tokensWithIndex:(List[String],Int))(implicit rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,Unit] = {
    tokensWithIndex match {
      case (t, l) if t.slice(rulesFileMetaData.valueCols, rulesFileMetaData.numCols).isEmpty =>
        Xor.left(RulesFileLoadError(s"in line $l all result tokens are empty"))
      case (t, l) if t.size != rulesFileMetaData.numCols =>
        Xor.left(RulesFileLoadError(s"in line $l number of columns is ${t.size}, should be ${rulesFileMetaData.numCols}"))
      case _ =>
        Xor.right(())
    }
  }

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):SectionRule = {
    val result = >>>(tokens.drop(rulesFileMetaData.valueCols).head, tokens.last.toBoolean)
    val values = tokens.take(rulesFileMetaData.valueCols)
    SectionRule(values.map(>>>(_,false)), result)
  }

  def createRuleSet(rulesFileMetaData:RulesFileMetaData, ruleTokens:List[List[String]], headings:List[String]):Xor[RulesFileLoadError,SectionRuleSet] = {
    Try {
      val rules = ruleTokens.map(createRule(_, rulesFileMetaData))
      SectionRuleSet(rulesFileMetaData.name, headings, rules)
    }
    match {
      case Success(sectionRuleSet) => Xor.right(sectionRuleSet)
      case Failure(e) => Xor.left(RulesFileLoadError(e.getMessage))
    }
  }

  def createErrorMessage(tokens:List[List[String]]):String = tokens.map(a => s"$a").mkString(" ")
}

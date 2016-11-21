package uk.gov.hmrc.decisionservice.ruleengine

import java.io.IOException

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.model._

import scala.io.Source
import scala.util.{Failure, Success, Try}

case class RulesFileMetaData(valueCols:Int, resultCols:Int, path:String, name:String){
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
          if (is == null) { throw new IOException(s"resource not found: ${rulesFileMetaData.path}") }
        using(Source.fromInputStream(is)) { res =>
          val tokens = res.getLines.map(_.split(Separator).map(_.trim).toList).toList
          val (headings::rest) = tokens
          val (rules, errorRules) = rest.partition { isValidRule(_, rulesFileMetaData) }
          errorRules match {
            case Nil => createRuleSet(rulesFileMetaData.name, rules.map(createRule(_, rulesFileMetaData)), headings)
            case l => throw new IOException(createErrorMessage(l)) // TODO refactor it out of try
          }
        }
      } match {
        case Success(content) => Xor.right(content)
        case Failure(e) =>
          Xor.left(RulesFileLoadError(e.getMessage))
      }
    }

  def isValidRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):Boolean = {
    tokens.size == rulesFileMetaData.numCols
  }

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):Rule

  def createRuleSet(name:String, rules:List[Rule], headings:List[String]):RuleSet

  def createErrorMessage(tokens:List[List[String]]):String = tokens.map(a => s"$a").mkString(" ")
}


object SectionRulesLoader extends RulesLoader {
  type ValueType = String
  type Rule = SectionRule
  type RuleSet = SectionRuleSet

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):SectionRule = {
    val result = CarryOverImpl(tokens.drop(rulesFileMetaData.valueCols).head, tokens.last.toBoolean)
    val values = tokens.take(rulesFileMetaData.valueCols)
    SectionRule(values, result)
  }

  def createRuleSet(name:String, rules:List[SectionRule], headings:List[String]):SectionRuleSet = {
    SectionRuleSet(name, headings, rules)
  }
}


object MatrixRulesLoader extends RulesLoader {
  type ValueType = CarryOver
  type Rule = MatrixRule
  type RuleSet = MatrixRuleSet

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):MatrixRule = {
    val result = MatrixDecisionImpl(tokens.last)
    val values = tokens.take(tokens.size-1).map(CarryOverImpl(_,false))
    MatrixRule(values, result)
  }

  def createRuleSet(name:String, rules:List[MatrixRule], headings:List[String]):MatrixRuleSet = {
    MatrixRuleSet(headings, rules)
  }
}

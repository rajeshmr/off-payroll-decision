package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{MatrixDecision, _}

import scala.io.Source
import scala.util.{Failure, Success, Try}

case class RulesFileMetaData(valueCols:Int, resultCols:Int, path:String){
  def numCols = valueCols + resultCols
}


trait RulesLoader {
  type ValueType
  type Rule  <: { def values:List[ValueType] }
  type RuleSet

  val Separator = ','

  def using[R <: { def close(): Unit }, B](resource: R)(f: R => B): B = try { f(resource) } finally { resource.close() }

  def load(rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,RuleSet] = {
      Try {
        val is = getClass.getResourceAsStream(rulesFileMetaData.path)
        val ruleSet = using(Source.fromInputStream(is)) { res =>
          val (h::t) = res.getLines.toList
          val headings = h.split(Separator).map(_.trim).toList
          val rules = (for (line <- t) yield {
            line.split(Separator).map(_.trim).toList match {
              case tokens if isValidRule(tokens, rulesFileMetaData) => createRule(tokens, rulesFileMetaData)
            }
          })
          createRuleSet(rules, headings)
        }
        ruleSet
      }
      match {
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

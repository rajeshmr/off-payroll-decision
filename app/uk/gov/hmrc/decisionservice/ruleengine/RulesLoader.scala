package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{MatrixDecision, _}

import scala.io.Source
import scala.util.{Failure, Success, Try}

case class RulesFileMetaData(values:List[String], results:List[String], path:String)


trait RulesLoader {
  type ValueType
  type Rule  <: { def values:List[ValueType]; def result:RuleResult }
  type RuleResult

  val Separator = ','

  def using[R <: { def close(): Unit }, B](resource: R)(f: R => B): B = try { f(resource) } finally { resource.close() }

  def load(rulesFileMetaData: RulesFileMetaData):Xor[RulesFileLoadError,List[Rule]] = {
    Try {
      val is = getClass.getResourceAsStream(rulesFileMetaData.path)
      using(Source.fromInputStream(is)) { res =>
        (for (line <- res.getLines.drop(1)) yield {
          line.split(Separator).map(_.trim).toList match {
            case tokens if isValidRule(tokens, rulesFileMetaData) => createRule(tokens, rulesFileMetaData)
          }
        }).toList
      }
    } match {
      case Success(content) => Xor.right(content)
      case Failure(e) => Xor.left(RulesFileLoadError(e.getMessage))
    }
  }

  def isValidRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):Boolean = {
    tokens.size == rulesFileMetaData.values.size + rulesFileMetaData.results.size
  }

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):Rule
}


object SectionRulesLoader extends RulesLoader {
  type ValueType = String
  type Rule = SectionRule
  type RuleResult = SectionCarryOver

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):SectionRule = {
    val result = SectionCarryOver(tokens.drop(rulesFileMetaData.values.size).head, tokens.last.toBoolean)
    val values = tokens.take(rulesFileMetaData.values.size)
    SectionRule(values, result)
  }
}


object MatrixRulesLoader extends RulesLoader {
  type ValueType = SectionCarryOver
  type Rule = MatrixRule
  type RuleResult = MatrixDecision

  def createRule(tokens:List[String], rulesFileMetaData: RulesFileMetaData):MatrixRule = {
    val result = MatrixDecision(tokens.last)
    val values = tokens.take(tokens.size-1).map(SectionCarryOver(_,false))
    MatrixRule(values, result)
  }
}

package uk.gov.hmrc.decisionservice.ruleengine

import java.io.IOException

import scala.io.Source
import scala.util.{Failure, Try}

object RulesFileReaderTokenizer {
  private val Separator = ','

  private def using[R <: { def close(): Unit }, B](resource: R)(f: R => B): B = try { f(resource) } finally { resource.close() }

  def tokenize(implicit rulesFileMetaData: RulesFileMetaData): Try[List[List[String]]] = {
    val is = getClass.getResourceAsStream(rulesFileMetaData.path)
    if (is == null) {
      Failure(new IOException(s"resource not found: ${rulesFileMetaData.path}"))
    }
    else {
      Try(using(Source.fromInputStream(is)) { source =>
        source.getLines.map(_.split(Separator).map(_.trim).toList).toList
      })
    }
  }
}

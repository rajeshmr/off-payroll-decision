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

package uk.gov.hmrc.decisionservice.controllers

import java.io.InputStream

import cats.data.Xor
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}

import scala.io.Source

/**
  * Created by habeeb on 11/11/2016.
  *
  * FIXME: this class is to be refactored!!
  *
  *
  */
object JsonValidator{

  val schemaPath: String = "/off-payroll-question-set-schema.json"

  object SuccessfulReport {
    def unapply(processingReport:ProcessingReport) = processingReport.isSuccess
  }

  object ProblemReport {
    def unapply(processingReport:ProcessingReport):Option[String] = Some(reportAsString(processingReport))
  }

  def validate(json : String) : Xor[String,Unit] = {
    val jsonSchemaFactory: JsonSchemaFactory = JsonSchemaFactory.byDefault()
    val stream = getClass.getResourceAsStream(schemaPath)
    val content = Source.fromInputStream(stream).mkString
    val jsonSchemaNode = JsonLoader.fromString(content)
    val schema = jsonSchemaFactory.getJsonSchema(jsonSchemaNode)
    val jsonNode = JsonLoader.fromString(json)

    schema.validate(jsonNode) match {
      case SuccessfulReport() => Xor.right(())
      case ProblemReport(s) => Xor.left(s)
    }
  }

  def reportAsString(report:ProcessingReport) = {
    import scala.collection.JavaConversions._
    report.iterator().map(_.getMessage).mkString("\n")
  }

}

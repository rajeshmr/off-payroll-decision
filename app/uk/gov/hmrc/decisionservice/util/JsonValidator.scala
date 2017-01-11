/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.decisionservice.util

import cats.data.Xor
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory

import scala.io.Source

trait JsonValidatorTrait {
  val schemaPath:String

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


object JsonResponseValidator extends JsonValidatorTrait {
  val schemaPath: String = "/schema/off-payroll-response-schema.json"
}

object JsonResponseStrictValidator extends JsonValidatorTrait {
  val schemaPath: String = "/schema/off-payroll-response-schema-strict.json"
}

object JsonRequestValidator extends JsonValidatorTrait {
  val schemaPath: String = "/schema/off-payroll-request-schema.json"
}

object JsonRequestStrictValidator extends JsonValidatorTrait {
  val schemaPath: String = "/schema/off-payroll-request-schema-strict.json"
}

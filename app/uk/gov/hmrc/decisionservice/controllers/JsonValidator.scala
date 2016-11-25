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

  def validate(json : String) : Boolean = {
    val jsonSchemaFactory: JsonSchemaFactory = JsonSchemaFactory.byDefault()

    val stream: InputStream = getClass.getResourceAsStream("/off-payroll-question-set-schema.json")
    val lines: String = Source.fromInputStream(stream).mkString
    val jsonSchemaNode: JsonNode = JsonLoader.fromString(lines)
    val schema: JsonSchema = jsonSchemaFactory.getJsonSchema(jsonSchemaNode)
    val jsonNode: JsonNode = JsonLoader.fromString(json)

    val report: ProcessingReport = schema.validate(jsonNode)

    return report.isSuccess
  }

}

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

package uk.gov.hmrc.decisionservice.util

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class JsonSchemaSpec extends UnitSpec {

  val FULL_EXAMPLE_REQUST_JSON_PATH: String = "/test-scenarios/full-example-request.json"
  val TEST_CASE_PATH: String = "/test-scenarios/flattenedTestCase.csv"
  val tryJson = FileReader.read(FULL_EXAMPLE_REQUST_JSON_PATH)

  "json schema" should {
    "validate correctly full example request json" in {
      tryJson.isSuccess shouldBe true
      val requestJsonString = tryJson.get
        val validationResult = JsonValidator.validate(requestJsonString)
        validationResult.isRight shouldBe true
    }
    "validate request created from a flattened use case" in {
      val testCasesTry = ScenarioReader.readFlattenedTestCases(TEST_CASE_PATH)
      testCasesTry.isSuccess shouldBe true
      val testCases = testCasesTry.get
      testCases.size shouldBe 1
      testCases.map{ testCase =>
        val request = testCase.request
        val requestJson = Json.toJson(request)
        val requestJsonString = Json.prettyPrint(requestJson)
        println(requestJsonString)
        val validationResult = JsonValidator.validate(requestJsonString)
        println(validationResult)
        validationResult.isRight shouldBe true
      }
    }
  }

}

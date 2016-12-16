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
  val TEST_CASE_PATH = "/schema/schema_checking_testcase.csv"
  val FULL_EXAMPLE_REQUEST_JSON_PATH = "/schema/full-example-request.json"
  val tryJson = FileReader.read(FULL_EXAMPLE_REQUEST_JSON_PATH)

  "json schema" should {
    "validate correctly full example request json" in {
      tryJson.isSuccess shouldBe true
      val requestJsonString = tryJson.get
        val validationResult = JsonValidator.validate(requestJsonString)
        validationResult.isRight shouldBe true
    }
    "validate request created from a flattened test case" in {
      val testCasesTry = ScenarioReader.readFlattenedTestCaseTransposed(TEST_CASE_PATH)
      testCasesTry.isSuccess shouldBe true
      val testCase = testCasesTry.get
        val request = testCase.request
      val requestJson = Json.toJson(request)
      val requestJsonString = Json.prettyPrint(requestJson)
      val validationResult = JsonValidator.validate(requestJsonString)
      validationResult.isRight shouldBe true
    }
  }

}

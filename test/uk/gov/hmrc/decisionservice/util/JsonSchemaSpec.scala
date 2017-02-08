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
import play.api.libs.json.Json
import uk.gov.hmrc.decisionservice.Versions
import uk.gov.hmrc.decisionservice.testutil.RequestAndDecision
import uk.gov.hmrc.play.test.UnitSpec

class JsonSchemaSpec extends UnitSpec {
  val TEST_CASE_PATH = "/schema/1.0.1-beta/schema-checking-testcase.csv"
  val FULL_EXAMPLE_REQUEST_JSON_PATH = "/schema/1.0.1-beta/off-payroll-request-sample.json"
  val FULL_RESPONSE = "/schema/1.0.1-beta/off-payroll-response-sample.json"
  val tryJson = FileReader.read(FULL_EXAMPLE_REQUEST_JSON_PATH)

  " A Json Schema" should {
    s"validate correctly full example request json should validate with the loose schema for version ${Versions.VERSION1}" in {
      tryJson.isSuccess shouldBe true
      val requestJsonString = tryJson.get
      val maybeValidator = JsonRequestValidatorFactory(Versions.VERSION1)
      maybeValidator.isDefined shouldBe true
      maybeValidator.map { validator =>
        val validationResult = validator.validate(requestJsonString)
        printValidationResult(validationResult)
        validationResult.isRight shouldBe true
      }
    }
  }

  it should {
    "validate a request with the Strict Schema" in {
      tryJson.isSuccess shouldBe true
      val requestJsonString = tryJson.get
      val maybeValidator = JsonRequestStrictValidatorFactory(Versions.VERSION1)
      maybeValidator.isDefined shouldBe true
      val validationResult = maybeValidator.get.validate(requestJsonString)
      printValidationResult(validationResult)
      validationResult.isRight shouldBe true
    }
  }

  it should {
    s"validate a full response with the lose schema for version ${Versions.VERSION1}" in {
      validateResponseWithSchema(Versions.VERSION1)
    }
  }

  it should {
    s"validate a full response with the lose schema for version ${Versions.VERSION2}" in {
      validateResponseWithSchema(Versions.VERSION2)
    }
  }

  it should {
    "validate a full response with the Strict Schema" in {
      tryJson.isSuccess shouldBe true
      val requestJsonString = FileReader.read(FULL_RESPONSE).get
      val maybeValidator = JsonResponseStrictValidatorFactory(Versions.VERSION1)
      maybeValidator.isDefined shouldBe true
      val validationResult = maybeValidator.get.validate(requestJsonString)
      printValidationResult(validationResult)
      validationResult.isRight shouldBe true
    }
  }

  it should {
    "validate request created from a flattened test case" in {
      val testCasesTry = RequestAndDecision.readFlattenedTransposed(TEST_CASE_PATH)
      testCasesTry.isSuccess shouldBe true
      val testCase = testCasesTry.get
      val request = testCase.request
      val requestJson = Json.toJson(request)
      val requestJsonString = Json.prettyPrint(requestJson)
      val maybeValidator = JsonRequestValidatorFactory(Versions.VERSION1)
      maybeValidator.isDefined shouldBe true
      val validationResult = maybeValidator.get.validate(requestJsonString)
      printValidationResult(validationResult)
      validationResult.isRight shouldBe true
    }
  }

  private def printValidationResult(result: Xor[String, Unit]) = {
    result.leftMap { report => {
      info(report)
    }
    }
  }

  def validateResponseWithSchema(version:String): Unit ={
    val responsePath = s"/schema/${version}/off-payroll-response-sample.json"
    tryJson.isSuccess shouldBe true
    val requestJsonString = FileReader.read(responsePath).get
    val maybeValidator = JsonResponseValidatorFactory(version)
    maybeValidator.isDefined shouldBe true
    val validationResult = maybeValidator.get.validate(requestJsonString)
    printValidationResult(validationResult)
    validationResult.isRight shouldBe true
  }

}

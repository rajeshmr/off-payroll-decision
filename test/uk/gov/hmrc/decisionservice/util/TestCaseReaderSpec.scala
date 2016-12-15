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

import uk.gov.hmrc.play.test.UnitSpec


class TestCaseReaderSpec extends UnitSpec {

  "test case reader " should {
    "read valid flattened test case file" in {
      val testCasesTry = ScenarioReader.readFlattenedTestCases("/test-scenarios/flattenedTestCases.csv")
      testCasesTry.isSuccess shouldBe true
      val testCases = testCasesTry.get
      testCases.size shouldBe 5
      testCases.foreach{
        _.request.interview.size shouldBe 3
      }
    }
    "read valid cluster test case file" in {
      val testCasesTry = ScenarioReader.readScenarios("/test-scenarios/clusterTestCases.csv")
      testCasesTry.isSuccess shouldBe true
      val testCases = testCasesTry.get
      testCases.size shouldBe 5
    }
  }

}

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

package uk.gov.hmrc.decisionservice.testutilspec

import uk.gov.hmrc.decisionservice.testutil.FactsAndDecision
import uk.gov.hmrc.play.test.UnitSpec


class FactsAndDecisionSpec extends UnitSpec {
  val FLATTENED_TEST_CASES = "/test-scenarios/test-scenario-reader/flattened_test_cases.csv"
  val FLATTENED_TEST_CASES_TRANSPOSED = "/test-scenarios/test-scenario-reader/flattened_test_case_transposed.csv"
  val CLUSTER_TEST_CASES = "/test-scenarios/test-scenario-reader/cluster_test_cases.csv"

  "test scenario reader" should {
    "read valid cluster test case file" in {
      val tryFactsAndDecision = FactsAndDecision.read(CLUSTER_TEST_CASES)
      tryFactsAndDecision.isSuccess shouldBe true
      val testCases = tryFactsAndDecision.get
      testCases should have size 5
    }
  }

}

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

package uk.gov.hmrc.decisionservice.controllers

import uk.gov.hmrc.decisionservice.Versions
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class MatrixCsvSpec extends UnitSpec with WithFakeApplication with DecisionControllerFinalCsvSpec {
  val TEST_CASE_NOT_MATCHED_1 = "/test-scenarios/single/matrix/scenario-final-not-matched-1.csv"
  val TEST_CASE_NOT_MATCHED_2 = "/test-scenarios/single/matrix/scenario-final-not-matched-2.csv"
  val TEST_CASE_OUTOFIR35 = "/test-scenarios/single/matrix/scenario-earlyexit-outofir35.csv"
  val TEST_CASE_INSIDE_IR35 = "/test-scenarios/single/matrix/scenario-decision-ir35.csv"
  val TEST_CASE_INSIDE_IR35_MATCH_FINANCIAL_RISK_BLANK = "/test-scenarios/single/matrix/scenario-decision-ir35-matches-financialRiskBlank.csv"
  val TEST_CASE_UNKNOWN = "/test-scenarios/single/matrix/scenario-decision-unknown.csv"
  val TEST_CASE_INSIDE_IR35_VERSION2 = s"/test-scenarios/${Versions.VERSION100_FINAL}/matrix/scenario-decision-ir35.csv"
  val TEST_CASE_UNKNOWN_VERSION2 = s"/test-scenarios/${Versions.VERSION100_FINAL}/matrix/scenario-decision-unknown.csv"
  val TEST_CASE_INSIDE_IR35_VERSION111_FINAL = s"/test-scenarios/${Versions.VERSION111_FINAL}/matrix/scenario-decision-insideIr35.csv"
  val TEST_CASE_INSIDE_IR35_VERSION111_FINAL_2 = s"/test-scenarios/${Versions.VERSION111_FINAL}/matrix/scenario-decision-insideIr35_2.csv"
  val TEST_CASE_UNKNOWN_VERSION111_FINAL = s"/test-scenarios/${Versions.VERSION111_FINAL}/matrix/scenario-decision-unknown.csv"

  "POST /decide" should {
    s"return 200 and correct response with the not matched decision (1) for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(TEST_CASE_NOT_MATCHED_1, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response with the not matched decision (2) for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(TEST_CASE_NOT_MATCHED_2, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response with the out IR35 decision for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(TEST_CASE_OUTOFIR35, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response with the inside IR35 decision for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(TEST_CASE_INSIDE_IR35, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response with the inside IR35 decision (matches Financial Risk Blank) for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(TEST_CASE_INSIDE_IR35_MATCH_FINANCIAL_RISK_BLANK, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response with the unknown decision for version ${Versions.VERSION101_BETA}" in {
      createRequestSendVerifyDecision(TEST_CASE_UNKNOWN, Versions.VERSION101_BETA)
    }
    s"return 200 and correct response with the inside IR35 decision for version ${Versions.VERSION100_FINAL}" in {
      createRequestSendVerifyDecision(TEST_CASE_INSIDE_IR35_VERSION2, Versions.VERSION100_FINAL)
    }
    s"return 200 and correct response with the unknown decision for version ${Versions.VERSION100_FINAL}" in {
      createRequestSendVerifyDecision(TEST_CASE_UNKNOWN_VERSION2, Versions.VERSION100_FINAL)
    }
    s"return 200 and correct response with the inside IR35 decision for version ${Versions.VERSION111_FINAL}" in {
      createRequestSendVerifyDecision(TEST_CASE_INSIDE_IR35_VERSION111_FINAL, Versions.VERSION111_FINAL)
    }
    s"return 200 and correct response with the inside IR35 decision for version ${Versions.VERSION111_FINAL} - 2" in {
      createRequestSendVerifyDecision(TEST_CASE_INSIDE_IR35_VERSION111_FINAL_2, Versions.VERSION111_FINAL)
    }
    s"return 200 and correct response with the unknown decision for version ${Versions.VERSION111_FINAL}" in {
      createRequestSendVerifyDecision(TEST_CASE_UNKNOWN_VERSION111_FINAL, Versions.VERSION111_FINAL)
    }
  }
}

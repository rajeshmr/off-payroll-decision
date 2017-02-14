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

/**
  * Created by work on 09/01/2017.
  */
class FinancialRiskBCSVSpec extends UnitSpec with WithFakeApplication with DecisionControllerFinalCsvSpec {
  val FINANCIAL_RISK_B_SCENARIO_0 = "/test-scenarios/single/financial-risk-b/scenario_0.csv"
  val FINANCIAL_RISK_B_SCENARIO_1 = "/test-scenarios/single/financial-risk-b/scenario_1.csv"
  val FINANCIAL_RISK_B_SCENARIO_2 = "/test-scenarios/single/financial-risk-b/scenario_2.csv"

  "POST /decide" should {
    s"return 200 and expected decision for financial risk b scenario 0 for version ${Versions.VERSION1}" in {
      createRequestSendVerifyDecision(FINANCIAL_RISK_B_SCENARIO_0, Versions.VERSION1)
    }
    s"return 200 and expected decision for financial risk b scenario 1 for version ${Versions.VERSION1}" in {
      createRequestSendVerifyDecision(FINANCIAL_RISK_B_SCENARIO_1, Versions.VERSION1)
    }
    s"return 200 and expected decision for financial risk b scenario 2 for version ${Versions.VERSION1}" in {
      createRequestSendVerifyDecision(FINANCIAL_RISK_B_SCENARIO_2, Versions.VERSION1)
    }
  }
}

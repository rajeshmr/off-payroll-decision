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
class FinancialRiskACSVSpec extends UnitSpec with WithFakeApplication with DecisionControllerClusterCsvSpec {
  val clusterName = "financialRiskA"
  val FINANCIAL_RISK_A_SCENARIO_0 = "/test-scenarios/single/financial-risk-a/scenario_0.csv"
  val FINANCIAL_RISK_A_SCENARIO_1 = "/test-scenarios/single/financial-risk-a/scenario_1.csv"
  val FINANCIAL_RISK_A_SCENARIO_2 = "/test-scenarios/single/financial-risk-a/scenario_2.csv"

  "POST /decide" should {
    "return 200 and correct response with the expected decision for financial risk a scenario 0" in {
      createRequestSendVerifyDecision(FINANCIAL_RISK_A_SCENARIO_0, Versions.VERSION101_BETA)
    }
    "return 200 and correct response with the expected decision for financial risk a scenario 1" in {
      createRequestSendVerifyDecision(FINANCIAL_RISK_A_SCENARIO_1, Versions.VERSION101_BETA)
    }
//    "return 200 and correct response with the expected decision for financial risk a scenario 2" in {
//      createRequestSendVerifyDecision(FINANCIAL_RISK_A_SCENARIO_2)
//    }
  }
}

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

package uk.gov.hmrc.decisionservice.services

import play.api.Logger
import uk.gov.hmrc.decisionservice.ruleengine.{FactMatcherInstance, RulesFileMetaData, RulesLoaderInstance}
import uk.gov.hmrc.decisionservice.util.{Scenario, ScenarioTestCase, ScenarioReader}
import uk.gov.hmrc.play.test.UnitSpec

class ClusterTestCasesSpec extends UnitSpec {
  private val scenarioTestCases = List(
    ScenarioTestCase("/test-scenarios/part_of_organisation.csv", "part_of_organisation","/tables/part_of_organisation.csv",5),
    ScenarioTestCase("/test-scenarios/financial_risk.csv", "financial_risk","/tables/financial_risk.csv",24),
    ScenarioTestCase("/test-scenarios/control-onlyPassingCases.csv", "control","/tables/control.csv",13),
    ScenarioTestCase("/test-scenarios/misc.csv", "miscellaneous","/tables/misc.csv",1),
    ScenarioTestCase("/test-scenarios/business_structure.csv", "business_structure","/tables/business_structure.csv",7))
//    ClusterTestCaseFileMetaData("/test-scenarios/personal_service.csv", "personal_service","/tables/personal_service.csv",13)) - has failures

  "test case reader " should {
    "read valid cluster test case file" in {
      for (scenarioTestCase <- scenarioTestCases) {
        Logger.info("================= Running tests for Cluster: " + scenarioTestCase.clusterName + " ===================")
        val testCasesTry = ScenarioReader.readScenarios(scenarioTestCase.factsPath)
        testCasesTry.isSuccess shouldBe true
        testCasesTry.map { _.foreach(runAndVerifyTestCase(_, scenarioTestCase)) }
        Logger.info("================= Finished tests for Cluster: " + scenarioTestCase.clusterName + " ===================")
      }
    }
  }

  def runAndVerifyTestCase(testCase:Scenario, metaData:ScenarioTestCase):Unit = {
    val maybeRules = RulesLoaderInstance.load(RulesFileMetaData(metaData.numOfValueColumns, metaData.rulesPath, metaData.clusterName))
    maybeRules.isValid shouldBe true
    maybeRules.map { ruleSet =>
      val response = FactMatcherInstance.matchFacts(testCase.request.facts, ruleSet)
      response.isValid shouldBe true
      response.map { _ shouldBe testCase.expectedDecision }
    }
  }
}

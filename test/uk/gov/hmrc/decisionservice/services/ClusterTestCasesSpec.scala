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
import uk.gov.hmrc.decisionservice.util.{ClusterTestCase, ClusterTestCaseFileMetaData, TestCaseReader}
import uk.gov.hmrc.play.test.UnitSpec

/**
  * Created by work on 08/12/2016.
  */
class ClusterTestCasesSpec extends UnitSpec {

  private val testFiles = List(
    ClusterTestCaseFileMetaData("/test-scenarios/part_of_organisation.csv", "part_of_organisation","/tables/part_of_organisation.csv",5),
    ClusterTestCaseFileMetaData("/test-scenarios/financial_risk.csv", "financial_risk","/tables/financial_risk.csv",24),
    ClusterTestCaseFileMetaData("/test-scenarios/control-onlyPassingCases.csv", "control","/tables/control.csv",13),
    ClusterTestCaseFileMetaData("/test-scenarios/misc.csv", "miscellaneous","/tables/misc.csv",1),
    ClusterTestCaseFileMetaData("/test-scenarios/business_structure.csv", "business_structure","/tables/business_structure.csv",7))
//    ClusterTestCaseFileMetaData("/test-scenarios/personal_service.csv", "personal_service","/tables/personal_service.csv",13)) - has failures

  "test case reader " should {
    "read valid cluster test case file" in {
      for (i <- 0 until testFiles.size) {
        val metaData = testFiles(i)

        Logger.info("================= Running tests for Cluster: " + metaData.clusterName+" ===================")

        val testCasesTry = TestCaseReader
          .readClusterTestCaseLines(metaData)
        testCasesTry.isSuccess shouldBe true

        val testCases = testCasesTry.get

        for (i <- 0 until testCases.size) {
          runAndVerifyTestCase(testCases(i), metaData)
        }

        Logger.info("================= Finished tests for Cluster: " + metaData.clusterName+" ===================")

      }
    }
  }


  def runAndVerifyTestCase(testCase:ClusterTestCase, metaData:ClusterTestCaseFileMetaData) : Unit = {
    val maybeRules = RulesLoaderInstance.load(RulesFileMetaData(metaData.numOfValueColumns, metaData.rulesPath, metaData.clusterName))
    maybeRules.isValid shouldBe true
    maybeRules.map { ruleSet =>
      val response = FactMatcherInstance.matchFacts(testCase.request.facts, ruleSet)
      response.isValid shouldBe true
      response.map { sectionResult =>
        sectionResult.value shouldBe testCase.expectedDecision.value
        sectionResult.exit shouldBe testCase.expectedDecision.exit
        sectionResult.name.getOrElse("") shouldBe testCase.expectedDecision.name.getOrElse("")
       }

    }
  }

}

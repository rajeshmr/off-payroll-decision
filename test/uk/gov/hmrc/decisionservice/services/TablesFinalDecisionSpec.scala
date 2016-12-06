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
import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.play.test.UnitSpec

class TablesFinalDecisionSpec extends UnitSpec {

  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (13, "/tables/control.csv", "control"),
      (24, "/tables/financial_risk.csv", "financial_risk"),
      (5,  "/tables/part_of_organisation.csv", "part_of_organisation"),
      (1,  "/tables/misc.csv", "miscellaneous"),
      (7,  "/tables/business_structure.csv", "business_structure"),
      (13, "/tables/personal_service.csv", "personal_service"),
      (6,  "/tables/matrix_of_matrices.csv", "matrix")
    ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
  }

  "decision service" should {
    "produce correct decision for control and financial risk" in {
      val facts =
      Facts(
        Map(
          "whenWorkHasToBeDone.workingPatternAgreed" -> >>>("yes"),
          "whenWorkHasToBeDone.noDefinedWorkingPattern" -> >>>("no"),
          "workerLevelOfExpertise.notToldByEnagagerHowToWork" -> >>>("no"),
          "workerLevelOfExpertise.couldBeToldByEngagerHowToWork" -> >>>("no"),
          "whenWorkHasToBeDone.workingPatternStipulated" -> >>>("no"),
          "workerDecideWhere.cannotFixWorkerLocation" -> >>>("no"),
          "workerDecideWhere.workerDecideWhere" -> >>>("no"),
          "whenWorkHasToBeDone.workinPatternAgreedDeadlines" -> >>>("no")
        ) ++
        Map(
          "workerMainIncome.incomeFixed" -> >>>("yes")
        ) ++
        Map(
          "workerReceivesBenefits" -> >>>("yes")
        ) ++
        Map(
          "contractrualObligationForSubstitute" -> >>>("yes"),
          "contractualObligationInPractise" -> >>>("yes"),
          "contractTermsWorkerPaysSubstitute" -> >>>("yes")
        ) ++
        Map(
          "workerVAT" -> >>>("yes"),
          "businessAccount" -> >>>("yes"),
          "advertiseForWork" -> >>>("yes"),
          "businessWebsite" -> >>>("no"),
          "workerPayForTraining" -> >>>("no"),
          "workerExpenseRunningBusinessPremises" -> >>>("no"),
          "workerPaysForInsurance" -> >>>("no")
        ) ++
        Map(
          "similarWork" -> >>>("yes")
        )
      )

      val maybeDecision = facts ==>: DecisionServiceTestInstance
      Logger.debug(s"decision=$maybeDecision")
      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "OutOfIR35"
      }
    }
  }
}

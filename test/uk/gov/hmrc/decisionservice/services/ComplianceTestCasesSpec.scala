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

import uk.gov.hmrc.decisionservice.model.api.Score
import uk.gov.hmrc.decisionservice.model.rules.{EmptyCarryOver, _}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.play.test.UnitSpec

object DecisionServiceTestInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  lazy val csvSectionMetadata = List(
    (13, "/tables/control.csv", "control"),
    (24, "/tables/financial_risk.csv", "financial_risk"),
    (5, "/tables/part_of_organisation.csv", "part_of_organisation"),
    (1, "/tables/misc.csv", "miscellaneous"),
    (7, "/tables/business_structure.csv", "business_structure"),
    (13, "/tables/personal_service.csv", "personal_service"),
    (6, "/tables/matrix_of_matrices.csv", "matrix")
  ).collect { case (q, f, n) => RulesFileMetaData(q, f, n) }
}

class ComplianceTestCasesSpec extends UnitSpec {
  val testCases = Map(
    "Barbara" ->
      Facts(Map(
        "toldWhatToDo" -> >>>("yes"),
        "engagerMovingWorker" -> >>>("yes"),
        "workerDecidingHowWorkIsDone" -> >>>("yes"),
        "workerLevelOfExpertise.notToldByEnagagerHowToWork" -> >>>("yes"),
        "whenWorkHasToBeDone.workinPatternAgreedDeadlines" -> >>>("yes"),
        "workerDecideWhere.couldFixWorkerLocation" -> >>>("yes"),
        "engagerPayForConsumablesMaterials" -> >>>("yes"),
        "engagerPayExpense" -> >>>("yes"),
        "workerMainIncome.incomeCalendarPeriods" -> >>>("yes"),
        "workerProvideAtTheirExpense.labourOnly" -> >>>("yes"),
        "engagerArrangeIfWorkerIsUnable" -> >>>("yes")
      ))
  )

  "decision service" should {
    "produce correct the same decision as given by the compliance cases" in {
      val USE_CASE_NAME = "Barbara"
      val maybeFacts = testCases.get(USE_CASE_NAME)
      maybeFacts.isDefined shouldBe true
      maybeFacts.map { facts =>
        val maybeDecision = facts ==>: DecisionServiceTestInstance
        maybeDecision.isValid shouldBe true
        maybeDecision.map { decision =>
          decision.value shouldBe "Unknown"  // should be inIR35
          val maybeBusinessStructureScore = Score.create(decision.facts).get("business_structure")
          maybeBusinessStructureScore.isDefined shouldBe true
          maybeBusinessStructureScore.map{businessStructureScore =>
            businessStructureScore shouldBe NotValidUseCase.value
          }
        }
      }
    }
  }
}

package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.service.DecisionService
import uk.gov.hmrc.play.test.UnitSpec

class ControlFinancialRiskMofMSpec extends UnitSpec {

  object DecisionServiceTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (13, 3, "/tables/control.csv", "control"),
      (24, 3, "/tables/financial_risk.csv", "financial_risk"),
      (5,  3, "/tables/Part of organisation.csv", "part_of_organisation"),
      (1,  3, "/tables/Misc.csv", "miscellaneous"),
      (6,  3, "/tables/Matrix of Matrices.csv", "matrix")
    ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
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
          "personal_service" -> >>>("medium")
        ) ++
        Map(
          "business_structure" -> >>>("low")
        ) ++
        Map(
          "similarWork" -> >>>("yes")
        )
      )

      val maybeDecision = facts ==>: DecisionServiceTestInstance
      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "OutOfIR35"
      }
    }
  }
}

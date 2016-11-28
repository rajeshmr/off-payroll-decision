package uk.gov.hmrc.decisionservice

import play.api.Logger
import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.service.DecisionService
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
      Logger.info(s"decision=$maybeDecision")
      maybeDecision.isRight shouldBe true
      maybeDecision.map { decision =>
        decision.value shouldBe "OutOfIR35"
      }
    }
  }
}

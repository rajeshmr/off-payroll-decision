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

package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData
import uk.gov.hmrc.decisionservice.service.DecisionService
import uk.gov.hmrc.play.test.UnitSpec

class DecisionServiceErrorSpec extends UnitSpec {

  object DecisionServiceNotExistingCsvTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (7, "business_structure_not_existing.csv", "BusinessStructure"),
      (9, "personal_service_not_existing.csv", "PersonalService"),
      (2, "/decisionservicespec/matrix.csv", "matrix")
    ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
  }

  object DecisionServiceCsvWithErrorsTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (7, "/decisionservicespec/business_structure_errors.csv", "BusinessStructure"),
      (9, "/decisionservicespec/personal_service_errors.csv", "PersonalService"),
      (2, "/decisionservicespec/matrix.csv", "matrix")
    ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
  }

  object DecisionServiceCsvWithBadMetadataTestInstance extends DecisionService {
    lazy val maybeSectionRules = loadSectionRules()
    val csvSectionMetadata = List(
      (17, "/decisionservicespec/business_structure.csv", "BusinessStructure"),
      (19, "/decisionservicespec/personal_service.csv", "PersonalService"),
      (12, "/decisionservicespec/matrix.csv", "matrix")
    ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
  }

  val facts = Facts(Map("8a" -> >>>("yes"), "8g" -> >>>("no"), "2" -> >>>("yes"), "10" -> >>>("yes")))

  "decision service with initialization error" should {
    "correctly report multiple aggregated error information" in {
      val maybeDecision = facts ==>: DecisionServiceNotExistingCsvTestInstance
      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error.message.contains("business_structure_not_existing.csv") shouldBe true
        error.message.contains("personal_service_not_existing.csv") shouldBe true
      }
    }
    "correctly report errors in csv files" in {
      val maybeDecision = facts ==>: DecisionServiceCsvWithErrorsTestInstance
      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error shouldBe a [DecisionServiceError]
        error.message.contains("line") shouldBe true
      }
    }
    "correctly report errors in metadata files" in {
      val maybeDecision = facts ==>: DecisionServiceCsvWithBadMetadataTestInstance
      maybeDecision.isLeft shouldBe true
      maybeDecision.leftMap { error =>
        error shouldBe a [DecisionServiceError]
        error.message.contains("line") shouldBe true
      }
    }
  }

}

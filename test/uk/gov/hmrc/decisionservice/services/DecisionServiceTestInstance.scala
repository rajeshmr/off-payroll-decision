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

import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData

object DecisionServiceTestInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  lazy override val extraRules = List(DecisionServiceInstance.businessStructureRule)
  val csvSectionMetadata = List(
    (5, "/tables/control.csv", "control"),
    (7,  "/tables/financial_risk_a.csv", "financial_risk_a"),
    (13, "/tables/financial_risk_b.csv", "financial_risk_b"),
    (4,  "/tables/part_and_parcel.csv", "part_and_parcel"),
    (14, "/tables/personal_service.csv", "personal_service"),
    (5,  "/tables/matrix_of_matrices.csv", "matrix")
  ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
}

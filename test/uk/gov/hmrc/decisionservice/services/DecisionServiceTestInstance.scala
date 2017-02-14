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

package uk.gov.hmrc.decisionservice.services

import uk.gov.hmrc.decisionservice.ruleengine.RulesFileMetaData

object DecisionServiceTestInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  lazy override val extraRules = List(DecisionServiceInstance.businessStructureRule)
  val csvSectionMetadata = List(
    (5, "/tables/1.0.1-beta/control.csv", "control"),
    (7, "/tables/1.0.1-beta/financial-risk-a.csv", "financialRiskA"),
    (13, "/tables/1.0.1-beta/financial-risk-b.csv", "financialRiskB"),
    (4, "/tables/1.0.1-beta/part-and-parcel.csv", "partAndParcel"),
    (14, "/tables/1.0.1-beta/personal-service.csv", "personalService"),
    (5, "/tables/1.0.1-beta/matrix-of-matrices.csv", "matrix")
  ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
}

object DecisionServiceTestInstance100final extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  lazy override val extraRules = List(DecisionServiceInstance.businessStructureRule)
  val csvSectionMetadata = List(
    (4, "/tables/1.0.0-final/control.csv", "control"),
    (7, "/tables/1.0.0-final/financial-risk.csv", "financialRisk"),
    (4, "/tables/1.0.0-final/part-and-parcel.csv", "partAndParcel"),
    (5, "/tables/1.0.0-final/personal-service.csv", "personalService"),
    (4, "/tables/1.0.0-final/matrix-of-matrices.csv", "matrix")
  ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
}

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

import cats.Semigroup
import cats.data.Validated
import uk.gov.hmrc.decisionservice.Validation
import uk.gov.hmrc.decisionservice.model.api.ErrorCodes._
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, RulesFileError}
import uk.gov.hmrc.decisionservice.ruleengine._

object ListSemigroup extends Semigroup[List[DecisionServiceError]] {
  override def combine(x: List[DecisionServiceError], y: List[DecisionServiceError]): List[DecisionServiceError] = x ::: y
}


trait DecisionService {

  implicit val listSemi = Semigroup(ListSemigroup)

  implicit val sectionRuleSetSemigroup = Semigroup(SectionRuleSet("", List(), List()))

  val ruleEngine:RuleEngine = RuleEngineInstance

  val maybeSectionRules:Validation[List[SectionRuleSet]]

  val csvSectionMetadata:List[RulesFileMetaData]

  def loadSectionRules():Validation[List[SectionRuleSet]] = {
    val maybeRules = csvSectionMetadata.map(RulesLoaderInstance.load(_))
    val combined = if (maybeRules.isEmpty)
      Validated.invalid(List(RulesFileError(MISSING_RULE_FILES, "missing rule files")))
    else
      maybeRules.reduceLeft(_ combine _)
    combined match {
      case Validated.Valid(_) => Validated.valid(maybeRules.collect {case Validated.Valid(a) => a})
      case Validated.Invalid(e) => Validated.invalid(e)
    }
  }

  def ==>:(facts:Facts):Validation[RuleEngineDecision] = {
    maybeSectionRules match {
      case Validated.Valid(sectionRules) =>
        ruleEngine.processRules(Rules(sectionRules),facts)
      case Validated.Invalid(e) => Validated.invalid(e)
    }
  }
}


object DecisionServiceInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  lazy val csvSectionMetadata = List(
    (13, "/tables/control.csv", "control"),
    (24, "/tables/financial_risk.csv", "financial_risk"),
    (5,  "/tables/part_of_organisation.csv", "part_of_organisation"),
    (1,  "/tables/misc.csv", "miscellaneous"),
    (7,  "/tables/business_structure.csv", "business_structure"),
    (13, "/tables/personal_service.csv", "personal_service"),
    (6,  "/tables/matrix_of_matrices.csv", "matrix")
  ).collect{case (q,f,n) => RulesFileMetaData(q,f,n)}
}

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

package uk.gov.hmrc.decisionservice.model

abstract class ConditionCollection(conditions: Map[String,String]) {
  def is(condition:String):String = conditions.getOrElse(condition, "undefined")
}

sealed trait CarryOver {
  val value:String
}

case class Substitution(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class SubstitutionCarryOver(value:String) extends CarryOver

case class Helper(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class HelperCarryOver(value:String) extends CarryOver

case class Control(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class ControlCarryOver(value:String) extends CarryOver

case class FinancialRisk(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class FinancialRiskCarryOver(value:String) extends CarryOver

case class BusinessStructure(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class BusinessStructureCarryOver(value:String) extends CarryOver

case class PartOfOrganization(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class PartOfOrganizationCarryOver(value:String) extends CarryOver

case class Decision(text:String)

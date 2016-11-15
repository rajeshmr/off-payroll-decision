package uk.gov.hmrc.decisionservice.model

abstract class ConditionCollection(conditions: Map[String,String]) {
  def is(condition:String):String = conditions.getOrElse(condition, "undefined")
}

sealed trait DCarryOver {
  val value:String
}

case class Substitution(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class SubstitutionCarryOver(value:String) extends DCarryOver

case class Helper(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class HelperCarryOver(value:String) extends DCarryOver

case class Control(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class ControlCarryOver(value:String) extends DCarryOver

case class FinancialRisk(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class FinancialRiskCarryOver(value:String) extends DCarryOver

case class BusinessStructure(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class BusinessStructureCarryOver(value:String) extends DCarryOver

case class PartOfOrganization(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class PartOfOrganizationCarryOver(value:String) extends DCarryOver

case class Decision(text:String)


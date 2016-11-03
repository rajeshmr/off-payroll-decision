package uk.gov.hmrc.decisionservice.model

abstract class ConditionCollection(conditions: Map[String,String]) {
  def is(condition:String):String = conditions.getOrElse(condition, "undefined")
}

trait CarryOver

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


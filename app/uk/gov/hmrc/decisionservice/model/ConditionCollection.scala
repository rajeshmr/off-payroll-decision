package uk.gov.hmrc.decisionservice.model

abstract class ConditionCollection(conditions: Map[String,String]) {
  def is(condition:String):String = conditions.getOrElse(condition, "undefined")
}

trait CarryOver

case class Substitution(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class SubstitutionCO(carryOver:String) extends CarryOver

case class Helper(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class HelperCO(carryOver:String) extends CarryOver

case class Control(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class ControlCO(carryOver:String) extends CarryOver

case class FinancialRisk(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class FinancialRiskCO(carryOver:String) extends CarryOver

case class BusinessStructure(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class BusinessStructureCO(carryOver:String) extends CarryOver

case class PartOfOrganization(conditions: Map[String,String]) extends ConditionCollection(conditions)
case class PartOfOrganizationCO(carryOver:String) extends CarryOver

case class Decision(text:String)


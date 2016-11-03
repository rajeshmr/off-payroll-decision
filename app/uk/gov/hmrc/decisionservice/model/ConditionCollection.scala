package uk.gov.hmrc.decisionservice.model

abstract class ConditionCollection(conditions: Map[String,String]) {
  def is(condition:String):String = conditions.getOrElse(condition, "undefined")
  var carryOver:String = "" // TODO remove
}

case class Substitution(conditions: Map[String,String]) extends ConditionCollection(conditions)

case class Helper(conditions: Map[String,String]) extends ConditionCollection(conditions)

case class Control(conditions: Map[String,String]) extends ConditionCollection(conditions)

case class FinancialRisk(conditions: Map[String,String]) extends ConditionCollection(conditions)

case class BusinessStructure(conditions: Map[String,String]) extends ConditionCollection(conditions)

case class PartOfOrganization(conditions: Map[String,String]) extends ConditionCollection(conditions)

case class Decision(text:String)


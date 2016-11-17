package uk.gov.hmrc.decisionservice.model.api

import play.api.libs.json.{Format, Json}


case class QuestionSet(version:String, sections:Map[String,Map[String,Boolean]])

object QuestionSet {
  implicit val questionSetFormat: Format[QuestionSet] = Json.format[QuestionSet]
}

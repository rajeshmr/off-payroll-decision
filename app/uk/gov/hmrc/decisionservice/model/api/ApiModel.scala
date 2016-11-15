package uk.gov.hmrc.decisionservice.model.api

import play.api.libs.json.{Format, Json}


case class Section(name:String, facts:Map[String,Boolean])

case class QuestionSet(version:String, sections:List[Section])

object Section {
  implicit val sectionFormat: Format[Section] = Json.format[Section]
}

object QuestionSet {
  implicit val questionSetFormat: Format[QuestionSet] = Json.format[QuestionSet]
}

package uk.gov.hmrc.decisionservice.model.api

import play.api.libs.json.{Format, Json}


case class Section(name:String, facts:Map[String,Boolean])

case class QuestionSet(version:String, sections:List[Section])

object Section {
  implicit val factSectionFormat: Format[Section] = Json.format[Section]
}

object QuestionSet {
  implicit val decisionRequestFormat: Format[QuestionSet] = Json.format[QuestionSet]
}

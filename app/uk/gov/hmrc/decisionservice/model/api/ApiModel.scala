package uk.gov.hmrc.decisionservice.model.api

import play.api.libs.json.{Format, JsError, JsSuccess, Json}


case class Fact(name:String, answer:Boolean)

case class FactSection(name:String, questions:List[Fact])

case class DecisionRequest(version:String, sections:List[FactSection])

object Fact {
  implicit val factFormat: Format[Fact] = Json.format[Fact]
}

object FactSection {
  implicit val factSectionFormat: Format[FactSection] = Json.format[FactSection]
}

object DecisionRequest {
  implicit val decisionRequestFormat: Format[DecisionRequest] = Json.format[DecisionRequest]
}


/**
 * TODO delete this object
 **/
object Temporatus extends App {
  val questions1 = List(Fact("1", true),  Fact("2", false))
  val questions2 = List(Fact("1", false), Fact("2", false), Fact("3", false))
  val questions3 = List(Fact("1", true),  Fact("2", true),  Fact("3", true))
  val sections = List(
    FactSection("personal-service", questions1),
    FactSection("helper", questions2),
    FactSection("control", questions3)
  )
  val x = DecisionRequest("1.0", sections)
  val j = Json.toJson(x)
//  println(Json.prettyPrint(j))

  val json =
    """
      |{
      |  "version" : "1.0",
      |  "sections" : [ {
      |    "name" : "personal-service",
      |    "questions" : [ {
      |      "id" : 1,
      |      "answer" : true
      |    }, {
      |      "id" : 2,
      |      "answer" : false
      |    }, {
      |      "id" : 3,
      |      "answer" : null
      |    } ]
      |  } ]
      |}
      |
    """.stripMargin
  val parsed = Json.parse(json)
  val oo = Json.fromJson[DecisionRequest](parsed)
  oo match {
    case JsSuccess(v,_) =>
      println(v)
    case JsError(jsonErrors) =>
      println(JsError.toFlatJson(jsonErrors))
  }
  println(parsed)

}

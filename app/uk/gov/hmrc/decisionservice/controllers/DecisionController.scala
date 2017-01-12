/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.decisionservice.controllers

import cats.data.Validated
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.Action
import uk.gov.hmrc.decisionservice.Validation
import uk.gov.hmrc.decisionservice.model.api.ErrorCodes._
import uk.gov.hmrc.decisionservice.model.api.{DecisionRequest, DecisionResponse, ErrorResponse, Score}
import uk.gov.hmrc.decisionservice.model.rules.{>>>, Facts}
import uk.gov.hmrc.decisionservice.ruleengine.RuleEngineDecision
import uk.gov.hmrc.decisionservice.services.{DecisionService, DecisionServiceInstance}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



trait DecisionController extends BaseController {
  val decisionService:DecisionService
  val logger = Logger("accesslog")

  def decide() = Action.async(parse.json) { implicit request =>
    request.body.validate[DecisionRequest] match {
      case JsSuccess(req, _) =>
        logger.info("{\"request\":" + Json.prettyPrint(request.body))
        doDecide(req).map {
          case Validated.Valid(decision) =>
            val response = Json.toJson(decisionToResponse(req, decision))
            logger.info("\"response\":" + Json.prettyPrint(response) + "}")
            Ok(response)
          case Validated.Invalid(error) =>
            val errorResponse = Json.toJson(ErrorResponse(error(0).code, error(0).message))
            logger.info("\"errorResponse\":" + Json.prettyPrint(errorResponse) + "}")
            BadRequest(errorResponse)
        }
      case JsError(jsonErrors) =>
        logger.info("{\"incorrectRequest\":" + jsonErrors + "}")
        Future.successful(BadRequest(Json.toJson(ErrorResponse(REQUEST_FORMAT, JsError.toJson(jsonErrors).toString()))))
    }
  }

  def doDecide(decisionRequest:DecisionRequest):Future[Validation[RuleEngineDecision]] = Future {
      requestToFacts(decisionRequest) ==>: decisionService
  }

  def requestToFacts(decisionRequest: DecisionRequest): Facts = {
    val listsOfStringPairs = decisionRequest.interview.toList.collect { case (a, b) => b.toList }.flatten
    Facts(listsOfStringPairs.collect { case (a, b) => (a, >>>(b)) }.toMap)
  }

  def decisionToResponse(decisionRequest:DecisionRequest, ruleEngineDecision: RuleEngineDecision):DecisionResponse = {
    DecisionResponse(
      decisionRequest.version,
      decisionRequest.correlationID,
      Score.create(ruleEngineDecision.facts), responseString(ruleEngineDecision))
  }

  def responseString(ruleEngineDecision: RuleEngineDecision):String = ruleEngineDecision.value.toLowerCase match {
    case "inir35" => "Inside IR35"
    case "outofir35" => "Outside IR35"
    case "unknown" => "Unknown"
    case _ => "Not Matched"
  }
}

object DecisionController extends DecisionController {
  lazy val decisionService = DecisionServiceInstance
}

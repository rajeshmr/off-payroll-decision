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

package uk.gov.hmrc.decisionservice.controllers

import cats.data.{Validated, Xor}
import play.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.Action
import uk.gov.hmrc.decisionservice.Validation
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
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

  def decide() = Action.async(parse.json) { implicit request =>
    request.body.validate[DecisionRequest] match {
      case JsSuccess(req, _) =>
        doDecide(req).map {
          case Validated.Valid(decision) => Ok(Json.toJson(decisionToResponse(req, decision)))
          case Validated.Invalid(error) => BadRequest(Json.toJson(ErrorResponse(error(0).code, error(0).message)))
        }
      case JsError(jsonErrors) =>
        Logger.debug(s"incorrect request: ${jsonErrors} ")
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
    DecisionResponse(decisionRequest.version, decisionRequest.correlationID, ruleEngineDecision.isFinal, Score.create(ruleEngineDecision.facts), ruleEngineDecision.value)
  }
}

object DecisionController extends DecisionController {
  lazy val decisionService = DecisionServiceInstance
}

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

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Action
import uk.gov.hmrc.decisionservice.model.analytics.Interview
import uk.gov.hmrc.decisionservice.repository.InterviewRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by work on 20/06/2017.
  */
class AnalyticsController @Inject() (repository:InterviewRepository) extends BaseController {

  def logInterview  = Action.async(parse.json) { implicit request =>
    Logger.debug(s"request: ${request.body.toString.replaceAll("\"", "")}")
    request.body.validate[Interview].fold(
      error    => Future.successful(BadRequest(JsError.toJson(error))),
      response => repository.save(response).map{
        case result if result.ok        => Ok
        case result                     => InternalServerError(result.writeErrors.mkString)
      }
    )
  }

}
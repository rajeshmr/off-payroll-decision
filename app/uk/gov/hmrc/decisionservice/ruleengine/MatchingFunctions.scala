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

package uk.gov.hmrc.decisionservice.ruleengine

import play.api.Logger
import uk.gov.hmrc.decisionservice.model.rules.{>>>, CarryOver, SectionRule}

object MatchingFunctions {
  def matches(sr: SectionRule, factValues: List[CarryOver]): Option[CarryOver] = {
    factValues.zip(sr.values).filterNot(>>>.equivalent(_)) match {
      case Nil =>
        Logger.debug(s"matched:\t${sr.values.map(_.value).mkString("\t,")}")
        Some(sr.result)
      case _ => None
    }
  }

  def countYes(sr: SectionRule, factValues: List[CarryOver]): Option[CarryOver] = {
    factValues match {
      case Nil => None
      case xs =>
        val count = xs.filter(_.value.toLowerCase == "yes").size
        val result = count match {
          case n if n <= 1 => >>>("low")
          case n if n >= 2 && n <= 3 => >>>("medium")
          case _ => >>>("high")
        }
        Logger.debug(s"number of yes's is ${count}, pseudo-match result is: ${result.value}")
        Some(result)
    }
  }
}

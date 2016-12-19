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

package uk.gov.hmrc.decisionservice.model.rules

import cats.Semigroup
import play.api.Logger

case class SectionRule(values:List[CarryOver], result:CarryOver, matchingFunction:(SectionRule,List[CarryOver]) => Option[CarryOver] = SectionRule.matches )

object SectionRule {
  def apply(fun:(SectionRule,List[CarryOver]) => Option[CarryOver]):SectionRule = new SectionRule(List(), EmptyCarryOver, fun)
  def matches(sr:SectionRule, factValues: List[CarryOver]):Option[CarryOver] = {
    factValues.zip(sr.values).filterNot(>>>.equivalent(_)) match {
      case Nil =>
        Logger.debug(s"matched:\t${sr.values.map(_.value).mkString("\t,")}")
        Some(sr.result)
      case _ => None
    }
  }
  def countOnes(sr:SectionRule, factValues: List[CarryOver]):Option[CarryOver] = {
    factValues.filter{_.value == "Yes"}.size match {
      case n if n <= 1 => Some(>>>("low"))
      case n if n >= 2 && n <= 3 => Some(>>>("medium"))
      case _ => Some(>>>("high"))
    }
  }
}

case class SectionRuleSet(section:String, headings:List[String],rules:List[SectionRule]) extends Semigroup[SectionRuleSet] {
  override def combine(x: SectionRuleSet, y: SectionRuleSet): SectionRuleSet = x
}

case class Rules(rules:List[SectionRuleSet])

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

package uk.gov.hmrc.decisionservice

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inspectors, LoneElement}
import uk.gov.hmrc.decisionservice.model.RulesFileLoadError
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec

class SectionRulesLoaderSpec extends UnitSpec with BeforeAndAfterEach with ScalaFutures with LoneElement with Inspectors with IntegrationPatience {

  val csvFilePath = "/business_structure.csv"
  val csvFilePathError = "/business_structure_error.csv"
  val csvMetadata = RulesFileMetaData(3, 2, csvFilePath)
  val csvMetadataError = RulesFileMetaData(3, 2, csvFilePathError)

  "section rules loader" should {
    "load section rules from a csv file" in {
      val maybeRules = SectionRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size 4
        ruleset.headings should have size 3
      }
    }
    "return error if file is not found" in {
      val maybeRules = SectionRulesLoader.load(RulesFileMetaData(3, 2, csvFilePath + "xx"))
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "return error if file contains invalid data" in {
      val maybeRules = SectionRulesLoader.load(csvMetadataError)
      maybeRules.isLeft shouldBe true
      maybeRules.leftMap { error =>
        error shouldBe a [RulesFileLoadError]
      }
    }
    "provide valid input for an inference against fact" in {
      val fact = Map(
        ("Q1" -> "yes"),
        ("Q2" -> "no"),
        ("Q3" -> "yes"))
      val maybeRules = SectionRulesLoader.load(csvMetadata)
      maybeRules.isRight shouldBe true
      maybeRules.map { ruleset =>
        ruleset.rules should have size 4
        ruleset.headings should have size 3
        val response = SectionFactMatcher.matchFacts(fact, ruleset)
        response.isRight shouldBe true
        response.map { sectionResult =>
          sectionResult.value should equal("low")
          sectionResult.exit should equal(true)
        }
      }
    }
  }
}

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

package uk.gov.hmrc.decisionservice.utils

import org.specs2.matcher.{MustExpectations, NumericMatchers}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ConfigReaderSpec extends UnitSpec with WithFakeApplication with MustExpectations with NumericMatchers {

  "configuration reader" should {
    "read configured list of strings" in {
      val list = ConfigReader.getStringList("scoreElements", List("abc"))
      list should contain theSameElementsAs List("control", "financial_risk", "part_of_organisation", "miscellaneous", "business_structure", "personal_service", "matrix")
    }
    "return default list if configured list not found" in {
      val list = ConfigReader.getStringList("path.not.found", List("abc"))
      list should contain theSameElementsAs List("abc")
    }
  }

}

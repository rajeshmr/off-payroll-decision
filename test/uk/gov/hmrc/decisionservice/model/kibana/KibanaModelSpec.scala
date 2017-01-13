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

package uk.gov.hmrc.decisionservice.model.kibana

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.test.UnitSpec

class KibanaModelSpec extends UnitSpec {
  "kibana index Scala object" should {
    "be correctly converted to json object" in {
      val kibanaIndex = KibanaIndex(KibanaIndexNested("decision", "act", 123))
      val jsValue:JsValue = Json.toJson(kibanaIndex)
      jsValue.toString shouldBe "{\"index\":{\"_index\":\"decision\",\"_type\":\"act\",\"_id\":123}}"
      val index = jsValue \\ "_index"
      val id = jsValue \\ "_id"
      index should have size 1
      id should have size 1
    }
  }
  "kibana row Scala object" should {
    "be correctly converted to kibana row line" in {
      val kibanaRow = KibanaRow(Map("a"->"a1", "b"->"b1"))
      kibanaRow.asLogLine shouldBe "{\"a\":\"a1\",\"b\":\"b1\"}"
    }
  }
}

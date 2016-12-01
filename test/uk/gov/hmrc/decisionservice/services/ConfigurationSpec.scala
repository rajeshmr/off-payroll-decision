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

package uk.gov.hmrc.decisionservice.services

import org.specs2.matcher.{MustExpectations, NumericMatchers}
import play.api.Play
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.collection.JavaConversions._

class ConfigurationSpec extends UnitSpec with WithFakeApplication with MustExpectations with NumericMatchers {

  val NUMBER_OF_CLUSTERS: Long = 7


  "configuration" should {
    "contain configurations for clusters" in {

      val maybeClustersConfigurations = Play.current.configuration.getConfigList("clusters")
      maybeClustersConfigurations.isDefined shouldBe true

      maybeClustersConfigurations.map { clustersConfigurations =>
        clustersConfigurations should have size NUMBER_OF_CLUSTERS
        clustersConfigurations.foreach { cluster =>
          println(s" ${cluster.getString("name")}  ${cluster.getString("path")}")
        }
      }

    }
  }

}

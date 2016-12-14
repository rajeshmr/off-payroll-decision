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

package uk.gov.hmrc.decisionservice.util

import java.io.IOException

import uk.gov.hmrc.decisionservice.model.api.DecisionRequest
import uk.gov.hmrc.decisionservice.model.rules.{>>>, CarryOver, Facts}

import scala.io.Source
import scala.util.{Failure, Try}

case class TestCase(expectedDecision:String, request:DecisionRequest)
case class TestCaseFileMetaData(path:String, version:String)

case class ClusterTestCase(expectedDecision:CarryOver, request:Facts)
case class ClusterTestCaseFileMetaData(factsPath:String, clusterName:String, rulesPath:String, numOfValueColumns:Int)


object TestCaseReader {
  private val SEPARATOR = ','
  private val NUM_OF_CLUSTER_RESULT_COLUMNS = 3

  private def using[R <: { def close(): Unit }, B](resource: R)(f: R => B): B = try { f(resource) } finally { resource.close() }

  def readFlattenedTestCaseLines(implicit metaData: TestCaseFileMetaData) : Try[List[TestCase]] = {
    val is = getClass.getResourceAsStream(metaData.path)
    if (is == null) {
      Failure(new IOException(s"resource not found: ${metaData.path}"))
    }
    else {
      Try(using(Source.fromInputStream(is)) { source =>
        val tokens = source.getLines.map(_.split(SEPARATOR).map(_.trim).toList).toList
        val clusterNames = tokens(0).dropRight(1)
        val tagNames = tokens(1)
        val answersAndDecision = tokens.drop(2)
        answersAndDecision.map(buildDecisionRequest(clusterNames, tagNames, _))
      })
    }
  }

  def buildDecisionRequest(clusterNames : List[String], tagNames : List[String], answersAndDecision : List[String]) : TestCase = {
    val expectedDecision = answersAndDecision.last
    val answers = answersAndDecision.dropRight(1)
    val interview:Map[String,Map[String,String]] = clusterNames.map{clusterName =>
      (clusterName -> (tagNames.zip(answers).toMap))
    }.toMap
    TestCase(expectedDecision, DecisionRequest("test-version", "test-correlation-id", interview))
  }

  def readClusterTestCaseLines(implicit metaData: ClusterTestCaseFileMetaData) : Try[List[ClusterTestCase]] = {
    val is = getClass.getResourceAsStream(metaData.factsPath)
    if (is == null) {
      Failure(new IOException(s"resource not found: ${metaData.factsPath}"))
    }
    else {
      Try(using(Source.fromInputStream(is)) { source =>
        val tuple = source.getLines.toList.splitAt(1)

        val tagNamesLine = tuple._1.head.split(SEPARATOR)
        val tagNamesAndResultColumns = tagNamesLine.splitAt(tagNamesLine.size - NUM_OF_CLUSTER_RESULT_COLUMNS)
        val tagNames = tagNamesAndResultColumns._1.toList

        val scenarios = tuple._2

        var clusterTestCases:List[ClusterTestCase] = List()

        for (i <- 0 until scenarios.size){
          val answersAndCarryOver = scenarios(i).split(SEPARATOR).toList
          val answersAndCarryOverTuple = answersAndCarryOver.splitAt(
            answersAndCarryOver.size - getNumberOfResultColumn(answersAndCarryOver, tagNamesLine.toList))

          val answers = answersAndCarryOverTuple._1
          val carryOver = >>>.apply(answersAndCarryOverTuple._2)

          clusterTestCases = clusterTestCases ::: List(buildClusterTestCase(tagNames, answers, carryOver))
        }
        clusterTestCases
      })
    }
  }

  def buildClusterTestCase(tagNames : List[String], answers : List[String], expectedCarryOver : CarryOver) : ClusterTestCase = {
    var facts:Map[String,CarryOver] = Map()

    for (i <- 0 until answers.size){
      facts += tagNames(i) -> >>>(answers(i))
    }

    ClusterTestCase(expectedCarryOver, Facts(facts))
  }

  def getNumberOfResultColumn(currentLine:List[String], headerRow:List[String]) : Int = {
    if(currentLine.size == headerRow.size){
      3
    }
    else
     2
  }

}


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

import cats.data.Xor
import uk.gov.hmrc.decisionservice.util.JsonValidator.validate
import uk.gov.hmrc.play.test.UnitSpec


class JsonValidatorSpec extends UnitSpec {

  val valid_twoSections = """
   |{
   |  "version": "1.0.0-beta",
   |  "correlationID": "12345",
   |  "interview": {
   |    "personalService": {
   |      "contractualObligationForSubstitute": "No",
   |      "contractualObligationInPractise": "Yes",
   |      "contractrualRightForSubstitute": "Yes",
   |      "actualRightToSendSubstitute": "Yes",
   |      "contractualRightReflectInPractise": "No",
   |      "engagerArrangeIfWorkerIsUnwillingOrUnable": "No",
   |      "possibleSubstituteRejection": "No",
   |      "contractTermsWorkerPaysSubstitute": "Yes",
   |      "workerSentActualSubstitute": "Yes",
   |      "actualSubstituteRejection": "Yes",
   |      "possibleHelper": "Yes",
   |      "wouldWorkerPayHelper": "Yes",
   |      "workerSentActualHelper": "No",
   |      "workerPayActualHelper": "Yes"
   |    },
   |    "control": {
   |      "toldWhatToDo": "No",
   |      "engagerMovingWorker": "Yes",
   |      "workerDecidingHowWorkIsDone": "No",
   |      "whenWorkHasToBeDone": "workingPatternStipulated",
   |      "workerDecideWhere": "couldFixWorkerLocation"
   |    }
   |  }
   |}
   """.stripMargin

  val valid_noAnswers = """{
                              "version": "89.90.73C",
                              "correlationID": "adipisicing ullamco",
                              "interview": {
                              "personalService": {}
                              }
                            }"""

  val invalid_missingCorrelationID = """{
                                  "version": "89.90.73C",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "No",
                                    "engagerArrangeWorker": "No",
                                    "contractualRightForSubstitute": "No",
                                    "workerPayActualHelper": "No",
                                    "workerSentActualHelper": "Yes",
                                    "contractrualObligationForSubstitute": "No",
                                    "contractTermsWorkerPaysSubstitute": "No"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "No",
                                    "workerRepresentsEngagerBusiness": "No",
                                    "contactWithEngagerCustomer": "No"
                                  },
                                  "miscellaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_missingVersion = """{
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "No",
                                    "engagerArrangeWorker": "No",
                                    "contractualRightForSubstitute": "No",
                                    "workerPayActualHelper": "No",
                                    "workerSentActualHelper": "Yes",
                                    "contractrualObligationForSubstitute": "No",
                                    "contractTermsWorkerPaysSubstitute": "No"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "No",
                                    "workerRepresentsEngagerBusiness": "No",
                                    "contactWithEngagerCustomer": "No"
                                  },
                                  "miscellaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_invalidAnswerValue = """
 |{
 |  "version": "1.0.0-beta",
 |  "correlationID": "12345",
 |  "interview": {
 |    "personalService": {
 |      "contractualObligationForSubstitute": "No",
 |      "contractualObligationInPractise": true,
 |      "contractrualRightForSubstitute": "Yes",
 |      "actualRightToSendSubstitute": "Yes",
 |      "contractualRightReflectInPractise": "No",
 |      "engagerArrangeIfWorkerIsUnwillingOrUnable": "No",
 |      "possibleSubstituteRejection": "No",
 |      "contractTermsWorkerPaysSubstitute": "Yes",
 |      "workerSentActualSubstitute": "Yes",
 |      "actualSubstituteRejection": "Yes",
 |      "possibleHelper": "Yes",
 |      "wouldWorkerPayHelper": "Yes",
 |      "workerSentActualHelper": "No",
 |      "workerPayActualHelper": "Yes"
 |    }
 |  }
 | } """.stripMargin

  val invalid_invalidSection = """
 |{
 |  "version": "1.0.0-beta",
 |  "correlationID": "12345",
 |  "interview": {
 |    "personalService": {
 |      "contractualObligationForSubstitute": "No",
 |      "contractualObligationInPractise": "Yes",
 |      "contractrualRightForSubstitute": "Yes",
 |      "actualRightToSendSubstitute": "Yes",
 |      "contractualRightReflectInPractise": "No",
 |      "engagerArrangeIfWorkerIsUnwillingOrUnable": "No",
 |      "possibleSubstituteRejection": "No",
 |      "contractTermsWorkerPaysSubstitute": "Yes",
 |      "workerSentActualSubstitute": "Yes",
 |      "actualSubstituteRejection": "Yes",
 |      "possibleHelper": "Yes",
 |      "wouldWorkerPayHelper": "Yes",
 |      "workerSentActualHelper": "No",
 |      "workerPayActualHelper": "Yes"
 |    },
 |    "invalidSection": {
 |      "toldWhatToDo": "No",
 |      "engagerMovingWorker": "Yes",
 |      "workerDecidingHowWorkIsDone": "No",
 |      "whenWorkHasToBeDone": "workingPatternStipulated",
 |      "workerDecideWhere": "couldFixWorkerLocation"
 |    }}}
 """.stripMargin


  val invalid_invalidEnum = """
                             |{
 |  "version": "1.0.0-beta",
 |  "correlationID": "12345",
 |  "interview": {
 |    "personalService": {
 |      "contractualObligationForSubstitute": "No",
 |      "contractualObligationInPractise": "Yes",
 |      "contractrualRightForSubstitute": "Yes",
 |      "actualRightToSendSubstitute": "Yes",
 |      "contractualRightReflectInPractise": "No",
 |      "engagerArrangeIfWorkerIsUnwillingOrUnable": "No",
 |      "possibleSubstituteRejection": "No",
 |      "contractTermsWorkerPaysSubstitute": "Yes",
 |      "workerSentActualSubstitute": "Yes",
 |      "actualSubstituteRejection": "Yes",
 |      "possibleHelper": "Yes",
 |      "wouldWorkerPayHelper": "Yes",
 |      "workerSentActualHelper": "No",
 |      "workerPayActualHelper": "Yes"
 |    },
 |    "control": {
 |      "toldWhatToDo": "No",
 |      "engagerMovingWorker": "Yes",
 |      "workerDecidingHowWorkIsDone": "No",
 |      "whenWorkHasToBeDone": "workingPatternStipulated",
 |      "workerDecideWhere": "imWellGood"
 |    }}}
 |    """.stripMargin

  val invalid_invalidEnum2 = """{
                                     "version": "5.4.2-b",
                                     "correlationID": "dolor dolor",
                                     "interview": {
                                       "personalService": {
                                         "contractualObligationForSubstitute": "No",
                                         "contractualObligationInPractise": "No"
                                       },
                                       "control": {
                                         "toldWhatToDo": "Yes",
                                         "engagerMovingWorker": "No",
                                         "whenWorkHasToBeDone": "allDayEveryDay"
                                       }
                                     }
                                   }"""


  "json validator" should {

    "return true for valid json" in {
      validate(valid_twoSections).isRight shouldBe true
    }

    "return true for valid json - no answers" in {
      validate(valid_noAnswers).isRight shouldBe true
    }

    "return false for invalid json - InvalidAnswerValue" in {
      verify(invalid_invalidAnswerValue, "string")
    }

    "return false for invalid json - missing Version" in {
      verify(invalid_missingVersion, "object has missing required properties")
    }

    "return false for invalid json - missing CorrelationID" in {
      verify(invalid_missingCorrelationID, "object has missing required properties")
    }

    "return false for invalid json - invalidSection" in {
      verify(invalid_invalidSection, "[\"invalidSection\"]")
    }

    "return true for valid json - empty interview" in {
      val valid_emptyInterview =
        """{
             "version": "15.16.1-S",
             "correlationID": "ut",
             "interview" : {}
        }"""
      validate(valid_emptyInterview).isRight shouldBe true
    }

    "return false for invalid json - invalidFormatVersionId - should be string" in {
      val invalid_versionIdType =
        """{
        "version": 342571,
        "correlationID": "ut",
        "interview" : {}
      }"""
      verify(invalid_versionIdType, "integer")
    }

    "return false for invalid json - invalidFormatVersionId" in {
      val invalid_versionId =
        """{
        "version": "001-SNAPSHOT",
        "correlationID": "ut",
        "interview" : {}
      }"""
      verify(invalid_versionId, "001-SNAPSHOT")
    }

    "return true for valid json - valid version id" in {
      val valid_versionId =
        """{
        "version": "0.0.1-alpha",
        "correlationID": "ut",
        "interview" : {}
      }"""
      validate(valid_versionId).isRight shouldBe true
    }

    "return false for invalid json - enum value is not valid" in {
      verify(invalid_invalidEnum, "instance value (\"imWellGood\") not found in enum")
    }

    "return false for invalid json - enum value is not valid2" in {
      verify(invalid_invalidEnum2, "instance value (\"allDayEveryDay\") not found in enum")
    }

  }

  val verify = verifyError(validate) _

  def verifyError(f:String => Xor[String,Unit])(s:String, expectedText:String):Unit = {
    val result = validate(s)
    result.isRight shouldBe false
    result.leftMap { report => {
      println(report)
      report.contains(expectedText) shouldBe true
    }
    }
  }

}

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
import uk.gov.hmrc.decisionservice.controllers.JsonValidator.validate
import uk.gov.hmrc.play.test.UnitSpec


class JsonValidatorSpec extends UnitSpec {

  val valid_withTwoSections = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "interview" : {
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "false",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  }}
                                }"""

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
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "false",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_missingVersion = """{
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "false",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_withQuotesAroundBoolean = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": ""true"",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "false",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""


  val invalid_withInvalidBooleanValue = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "123ABC",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_withInvalidSection = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "invalidSection": {
                                    "invalidQuestion1": "false",
                                    "invalidQuestion2": "false",
                                    "invalidQuestion3": "true"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_withoutRequiredSection = """{
                                           "version": "15.16.1-S",
                                           "correlationID": "ut",
                                           "businessStructure": {
                                             "workerVAT": "true"
                                           },
                                           "financialRisk": {
                                             "engagerPayForConsumablesMaterials": "false"
                                           },
                                           "control": {
                                             "engagerMovingWorker": "false",
                                             "workerDecidingHowWorkIsDone": "true",
                                             "whenWorkHasToBeDone": "free"
                                           }
                                         }"""


  val invalid_withVersionIdAsNumber = """{
                                  "version": 234231,
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "false",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_withInvalidFormatVersionId = """{
                                  "version": "001-SNAPSHOT",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": "false",
                                    "engagerArrangeWorker": "false",
                                    "contractualRightForSubstitute": "false",
                                    "workerPayActualHelper": "false",
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": "false",
                                    "contractTermsWorkerPaysSubstitute": "false"
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "false",
                                    "workerRepresentsEngagerBusiness": "false",
                                    "contactWithEngagerCustomer": "false"
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  val invalid_withInvalidEnum = """{
                                   "version": "78.8.18Q",
                                   "correlationID": "dolor quis cillum velit in",
                                   "personalService": {
                                     "contractrualObligationForSubstitute": "false",
                                     "workerSentActualSubstitiute": "false",
                                     "workerSentActualHelper": "true",
                                     "engagerArrangeWorker": "true",
                                     "possibleHelper": "true"
                                   },
                                   "partOfOrganisation": {
                                     "contactWithEngagerCustomer": "true",
                                     "workerReceivesBenefits": "false",
                                     "workerAsLineManager": "false"
                                   },
                                   "businessStructure": {
                                     "businessWebsite": "true",
                                     "businesAccount": "false",
                                     "workerPayForTraining": "false"
                                   },
                                   "control": {
                                     "workerDecideWhere": "workerDecideWhere",
                                     "workerLevelOfExpertise": "imWellGood"
                                   },
                                   "miscalaneous": {}
                                 }"""

  val invalid_withInvalidEnum2 = """{
                                     "version": "5.4.2-b",
                                     "correlationID": "dolor dolor",
                                     "personalService": {
                                       "workerSentActualHelper": "false",
                                       "workerSentActualSubstitiute": "false"
                                     },
                                     "financialRisk": {
                                       "workerProvideConsumablesMaterials": "true",
                                       "engagerPayExpense": "false",
                                       "workerMainIncome": "allDayEveryDay"
                                     }
                                   }"""


  "json validator" should {

    "return true for valid json" in {
      validate(valid_withTwoSections).isRight shouldBe true
    }

    "return true for valid json - no answers" in {
      validate(valid_noAnswers).isRight shouldBe true
    }

//    "return false for invalid json - InvalidBooleanValue" in {
//      verify(invalid_withInvalidBooleanValue, "string")
//    }
//
//    "return false for invalid json - QuotesAroundBoolean" in {
//      verify(invalid_withQuotesAroundBoolean, "string")
//    }

    "return false for invalid json - missing Version" in {
      verify(invalid_missingVersion, "object has missing required properties")
    }

    "return false for invalid json - missing CorrelationID" in {
      verify(invalid_missingCorrelationID, "object has missing required properties")
    }

    "return false for invalid json - invalidSection" in {
      verify(invalid_withInvalidSection, "object instance has properties which are not allowed by the schema")
    }

//    "return false for invalid json - withoutPersonalService" in {
//      verify(invalid_withoutRequiredSection, "personalService")
//    }
//
//    "return false for invalid json - invalidFormatVersionId - should be string" in {
//      verify(invalid_withVersionIdAsNumber, "integer")
//    }
//
//    "return false for invalid json - invalidFormatVersionId" in {
//      verify(invalid_withInvalidFormatVersionId, "001-SNAPSHOT")
//    }
//
//    "return false for invalid json - enum value is not valid" in {
//      verify(invalid_withInvalidEnum, "imWellGood")
//    }
//
//    "return false for invalid json - enum value is not valid2" in {
//      verify(invalid_withInvalidEnum2, "allDayEveryDay")
//    }

  }

  val verify = verifyError(validate) _

  def verifyError(f:String => Xor[String,Unit])(s:String, expectedText:String):Unit = {
    val result = validate(s)
    result.isRight shouldBe false
    result.leftMap { report =>
      report.contains(expectedText) shouldBe true
    }
  }

}

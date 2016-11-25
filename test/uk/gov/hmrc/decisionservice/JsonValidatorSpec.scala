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

import uk.gov.hmrc.decisionservice.controllers.JsonValidator
import uk.gov.hmrc.play.test.UnitSpec

class JsonValidatorSpec extends UnitSpec {

  var valid_withTwoSections = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": false,
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  }
                                }"""

  var valid_noAnswers = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {}
                                }"""

  var invalid_missingCorrelationID = """{
                                  "version": "89.90.73C",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": false,
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  var invalid_missingVersion = """{
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": false,
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  var invalid_withQuotesAroundBoolean = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": "true",
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": false,
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""


  var invalid_withInvalidBooleanValue = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": "123ABC",
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  var invalid_withInvalidSection = """{
                                  "version": "89.90.73C",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "invalidSection": {
                                    "invalidQuestion1": false,
                                    "invalidQuestion2": false,
                                    "invalidQuestion3": true
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  var invalid_withoutRequiredSection = """{
                                           "version": "15.16.1-S",
                                           "correlationID": "ut",
                                           "businessStructure": {
                                             "workerVAT": true
                                           },
                                           "financialRisk": {
                                             "engagerPayForConsumablesMaterials": false
                                           },
                                           "control": {
                                             "engagerMovingWorker": false,
                                             "workerDecidingHowWorkIsDone": true,
                                             "whenWorkHasToBeDone": "free"
                                           }
                                         }"""


  var invalid_withVersionIdAsNumber = """{
                                  "version": 234231,
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": false,
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  var invalid_withInvalidFormatVersionId = """{
                                  "version": "001-SNAPSHOT",
                                  "correlationID": "adipisicing ullamco",
                                  "personalService": {
                                    "workerSentActualSubstitiute": false,
                                    "engagerArrangeWorker": false,
                                    "contractualRightForSubstitute": false,
                                    "workerPayActualHelper": false,
                                    "workerSentActualHelper": true,
                                    "contractrualObligationForSubstitute": false,
                                    "contractTermsWorkerPaysSubstitute": false
                                  },
                                  "partOfOrganisation": {
                                    "workerAsLineManager": false,
                                    "workerRepresentsEngagerBusiness": false,
                                    "contactWithEngagerCustomer": false
                                  },
                                  "miscalaneous": {},
                                  "businessStructure": {}
                                }"""

  var invalid_withInvalidEnum = """{
                                               "version": "78.8.18Q",
                                               "correlationID": "dolor quis cillum velit in",
                                               "personalService": {
                                                 "contractrualObligationForSubstitute": false,
                                                 "workerSentActualSubstitiute": false,
                                                 "workerSentActualHelper": true,
                                                 "engagerArrangeWorker": true,
                                                 "possibleHelper": true
                                               },
                                               "partOfOrganisation": {
                                                 "contactWithEngagerCustomer": true,
                                                 "workerReceivesBenefits": false,
                                                 "workerAsLineManager": false
                                               },
                                               "businessStructure": {
                                                 "businessWebsite": true,
                                                 "businesAccount": false,
                                                 "workerPayForTraining": false
                                               },
                                               "control": {
                                                 "workerDecideWhere": "workerDecideWhere",
                                                 "workerLevelOfExpertise": "imWellGood"
                                               },
                                               "miscalaneous": {}
                                             }"""

  var invalid_withInvalidEnum2 = """{
                                     "version": "5.4.2-b",
                                     "correlationID": "dolor dolor",
                                     "personalService": {
                                       "workerSentActualHelper": false,
                                       "workerSentActualSubstitiute": false
                                     },
                                     "financialRisk": {
                                       "workerProvideConsumablesMaterials": true,
                                       "engagerPayExpense": false,
                                       "workerMainIncome": "allDayEveryDay"
                                     }
                                   }"""


  "json validator" should {

    "return true for valid json" in {
      JsonValidator.validate(valid_withTwoSections) shouldBe true
    }

    "return true for valid json - no answers" in {
      JsonValidator.validate(valid_noAnswers) shouldBe true
    }

    "return false for invalid json - InvalidBooleanValue" in {
      JsonValidator.validate(invalid_withInvalidBooleanValue) shouldBe false
    }

    "return false for invalid json - QuotesAroundBoolean" in {
      JsonValidator.validate(invalid_withQuotesAroundBoolean) shouldBe false
    }

    "return false for invalid json - missingVersion" in {
      JsonValidator.validate(invalid_missingVersion) shouldBe false
    }

    "return false for invalid json - missingCorrelationID" in {
      JsonValidator.validate(invalid_missingCorrelationID) shouldBe false
    }

    "return false for invalid json - invalidSection" in {
      JsonValidator.validate(invalid_withInvalidSection) shouldBe false
    }

    "return false for invalid json - withoutPersonalService" in {
      JsonValidator.validate(invalid_withoutRequiredSection) shouldBe false
    }

    "return false for invalid json - invalidFormatVersionId - should be string" in {
      JsonValidator.validate(invalid_withVersionIdAsNumber) shouldBe false
    }

    "return false for invalid json - invalidFormatVersionId" in {
      JsonValidator.validate(invalid_withInvalidFormatVersionId) shouldBe false
    }

    "return false for invalid json - enum value is not valid" in {
      JsonValidator.validate(invalid_withInvalidEnum) shouldBe false
    }

    "return false for invalid json - enum value is not valid2" in {
      JsonValidator.validate(invalid_withInvalidEnum2) shouldBe false
    }

  }

}

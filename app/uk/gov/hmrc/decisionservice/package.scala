package uk.gov.hmrc

import cats.data.Validated
import uk.gov.hmrc.decisionservice.model.DecisionServiceError

package object decisionservice {

  type Validation[T] = Validated[List[DecisionServiceError],T]

}

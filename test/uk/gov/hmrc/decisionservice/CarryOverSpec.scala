package uk.gov.hmrc.decisionservice

import uk.gov.hmrc.decisionservice.model.rules.{>>>, EmptyCarryOver, NotValidUseCase}
import uk.gov.hmrc.play.test.UnitSpec

class CarryOverSpec extends UnitSpec {

  "carry over object" should {
    "produce correct string when converted to string" in {
      NotValidUseCase.toString shouldBe "NotValidUseCase"
      EmptyCarryOver.toString shouldBe "EmptyCarryOver"
      >>>("abc").toString shouldBe ">>>(abc)"
      >>>("").toString shouldBe ">>>()"
      >>>("abc",false).toString shouldBe ">>>(abc)"
      >>>("abc",true).toString shouldBe ">>>(abc,true)"
      >>>("abc", false, Some("factName")).toString shouldBe ">>>(abc,factName)"
      >>>("abc", true, Some("factName")).toString shouldBe ">>>(abc,true,factName)"
    }
  }

}

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
    "produce correct equivalence results" in {
      >>>.equivalent((>>>(""),>>>(""))) shouldBe true
      >>>.equivalent((>>>(""),>>>("a"))) shouldBe false
      >>>.equivalent((>>>("a"),>>>(""))) shouldBe true
    }
    "produce correct empty position sets" in {
      >>>.emptyPositions(Seq(>>>(""),>>>("a"),>>>(""),>>>("a"))) should contain theSameElementsAs(Seq(0,2))
      >>>.emptyPositions(Seq(>>>("a"),>>>(""),>>>(""),>>>(""))) should contain theSameElementsAs(Seq(1,2,3))
      >>>.emptyPositions(Seq(>>>("a"),>>>("",true,Some("abc")),>>>(""),>>>("",true))) should contain theSameElementsAs(Seq(1,2,3))
    }
  }

}

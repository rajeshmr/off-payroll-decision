package uk.gov.hmrc.decisionservice

import cats.data.Xor
import org.scalacheck.{Gen, Prop, Properties}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec

object PersonalServiceCheck extends Properties("personal service check") with UnitSpec {
  val csvMetadata = RulesFileMetaData(9, 2, "/personal_service.csv")

  def prettyPrint(m:Map[String,String]):Unit = {
    val s = m.keySet.toList.sorted.map(m(_)).map(a => if (a.length == 2) a + " " else a)
    print(s.mkString(" "))
  }

  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    Map("2" -> y(0), "3" -> y(1), "4" -> y(2), "5" -> y(3), "6" -> y(4), "7" -> y(5), "8" -> y(6), "9" -> y(7), "10" -> y(8))
  }

  val maybeRules = SectionRulesLoader.load(csvMetadata)

  property("rule should match all possible facts") =
    Prop.forAll(gen) { m =>
      println
      prettyPrint(m)
      maybeRules.map { ruleSet =>
        val response = SectionFactMatcher.matchFacts(m, ruleSet)
        response match {
          case Xor.Right(sectionResult) =>
            print(s"    ${sectionResult.value} ${sectionResult.exit}")
          case Xor.Left(e) =>
            print(s"    $e")
        }
      }
      true
    }

}

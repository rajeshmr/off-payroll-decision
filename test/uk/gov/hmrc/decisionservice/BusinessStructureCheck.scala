package uk.gov.hmrc.decisionservice
import cats.data.Xor
import org.scalacheck.{Gen, Prop, Properties}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec

object BusinessStructureCheck extends Properties("business structure check") with UnitSpec {
  val csvFilePath = "/business_structure.csv"
  val csvMetadata = RulesFileMetaData(7, 2, csvFilePath)

  def prettyPrint(m:Map[String,String]):Unit = {
    val s = m.keySet.toList.sorted.map(m(_)).map(a => if (a.length == 2) a + " " else a)
    print(s.mkString(" "))
  }

  val gen = for {
    y <- Gen.listOfN(7, Gen.oneOf[String]("Yes", "No"))
  } yield {
    Map("8a" -> y(0), "8b" -> y(1), "8c" -> y(2), "8d" -> y(3), "8e" -> y(4), "8f" -> y(5), "8g" -> y(6))
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
            sectionResult.exit should equal(true)
            print(s"    ${sectionResult.value}")
          case Xor.Left(e) =>
            print(s"    $e")
        }
      }
      true
    }

}

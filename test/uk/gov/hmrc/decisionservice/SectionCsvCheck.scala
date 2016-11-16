package uk.gov.hmrc.decisionservice
import cats.data.Xor
import org.scalacheck.{Gen, Prop, Properties}
import uk.gov.hmrc.decisionservice.model.RulesFileLoadError
import uk.gov.hmrc.decisionservice.model.rules.SectionRuleSet
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec


trait CsvCheck {
  def prettyPrint(m:Map[String,String]):Unit = print(m.keySet.toList.sorted.map(m(_)).mkString("\t"))
  def check(m:Map[String,String],maybeRules:Xor[RulesFileLoadError,SectionRuleSet]) = {
    println
    prettyPrint(m)
    maybeRules.map { ruleSet =>
      val response = SectionFactMatcher.matchFacts(m, ruleSet)
      response match {
        case Xor.Right(sectionResult) =>
          print(s"\t${sectionResult.value}")
        case Xor.Left(e) =>
          print(s"\t$e")
      }
    }
  }
}


object BusinessStructureCheck extends Properties("business structure check") with CsvCheck with UnitSpec {
  val csvMetadata = RulesFileMetaData(7, 2, "/business_structure.csv")
  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    Map("8a" -> y(0), "8b" -> y(1), "8c" -> y(2), "8d" -> y(3), "8e" -> y(4), "8f" -> y(5), "8g" -> y(6))
  }
  val maybeRules = SectionRulesLoader.load(csvMetadata)
  property("rule should match all possible facts") =
    Prop.forAll(gen) { m =>
      check(m,maybeRules)
      true
    }
}


object PersonalServiceCheck extends Properties("personal service check") with CsvCheck with UnitSpec {
  val csvMetadata = RulesFileMetaData(9, 2, "/personal_service.csv")
  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    Map("2" -> y(0), "3" -> y(1), "4" -> y(2), "5" -> y(3), "6" -> y(4), "7" -> y(5), "8" -> y(6), "9" -> y(7), "10" -> y(8))
  }
  val maybeRules = SectionRulesLoader.load(csvMetadata)
  property("rule should match all possible facts") =
    Prop.forAll(gen) { m =>
      check(m,maybeRules)
      true
    }
}

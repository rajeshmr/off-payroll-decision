package uk.gov.hmrc.decisionservice
import cats.data.Xor
import org.scalacheck.{Gen, Prop, Properties}
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, >>>, SectionRuleSet}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}
import uk.gov.hmrc.play.test.UnitSpec


trait CsvCheck {
  def prettyPrint(m: Map[String, CarryOver]): Unit = print(m.keySet.toList.sorted.map(a=>s"${a} ${m(a)}").mkString("\t"))

  def check(l: List[String], ruleSet: SectionRuleSet):Boolean = {
    println
    val ll = l map (>>>(_,false))
    val pairs = ruleSet.headings zip ll
    val m = Map(pairs: _*)
    prettyPrint(m)
    val response = SectionFactMatcher.matchFacts(m, ruleSet)
    response match {
      case Xor.Right(sectionResult) =>
        print(s"\t${sectionResult.value}")
      case Xor.Left(e) =>
        print(s"\t$e")
    }
    true
  }
}

object BusinessStructureCheck extends Properties("business structure check") with CsvCheck with UnitSpec {
  val csvMetadata = RulesFileMetaData(7, 2, "/business_structure.csv", "BusinessStructure")
  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    y
  }
  val maybeRules = SectionRulesLoader.load(csvMetadata)
  maybeRules.map { ruleSet =>
    property("rule should match all possible facts") =
      Prop.forAll(gen) { l =>
        check(l, ruleSet)
      }
  }
}


object PersonalServiceCheck extends Properties("personal service check") with CsvCheck with UnitSpec {
  val csvMetadata = RulesFileMetaData(9, 2, "/personal_service.csv", "PersonalService")
  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    y
  }
  val maybeRules = SectionRulesLoader.load(csvMetadata)
  maybeRules.map { ruleSet =>
    property("rule should match all possible facts") =
      Prop.forAll(gen) { l =>
        check(l, ruleSet)
      }
  }
}

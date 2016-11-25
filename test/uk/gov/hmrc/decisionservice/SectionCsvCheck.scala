package uk.gov.hmrc.decisionservice
import cats.data.Xor
import org.scalacheck.{Gen, Prop, Properties}
import uk.gov.hmrc.decisionservice.model.rules.{CarryOver, >>>, SectionRuleSet}
import uk.gov.hmrc.decisionservice.ruleengine.{RulesFileMetaData, FactMatcherInstance, RulesLoaderInstance}
import uk.gov.hmrc.play.test.UnitSpec


trait CsvCheck {

  def print(x:Any) = {}
  def println() = {}
  def println(x:Any) = {}

  def prettyPrint(m: Map[String, CarryOver]): Unit = print(m.keySet.toList.sorted.map(a=>s"${a} ${m(a).value}").mkString("\t"))

  def check(l: List[String], ruleSet: SectionRuleSet):Boolean = {
    println
    val ll = l map (>>>(_))
    val pairs = ruleSet.headings zip ll
    val m = Map(pairs: _*)
    prettyPrint(m)
    val response = FactMatcherInstance.matchFacts(m, ruleSet)
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
  val csvMetadata = RulesFileMetaData(7, "/business_structure.csv", "BusinessStructure")
  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    y
  }
  val maybeRules = RulesLoaderInstance.load(csvMetadata)
  maybeRules.map { ruleSet =>
    property("rule should match all possible facts") =
      Prop.forAll(gen) { l =>
        check(l, ruleSet)
      }
  }
}


object PersonalServiceCheck extends Properties("personal service check") with CsvCheck with UnitSpec {
  val csvMetadata = RulesFileMetaData(9, "/personal_service.csv", "PersonalService")
  val gen = for {
    y <- Gen.listOfN(csvMetadata.valueCols, Gen.oneOf[String]("Yes", "No"))
  } yield {
    y
  }
  val maybeRules = RulesLoaderInstance.load(csvMetadata)
  maybeRules.map { ruleSet =>
    property("rule should match all possible facts") =
      Prop.forAll(gen) { l =>
        check(l, ruleSet)
      }
  }
}

package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, RulesFileLoadError}
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine._


trait DecisionService {
  type CarryOverFacts = Map[String,CarryOver]

  val maybeSectionRules:Xor[DecisionServiceError,List[SectionRuleSet]]

  val csvSectionMetadata:List[RulesFileMetaData]

  def loadSectionRules():Xor[DecisionServiceError,List[SectionRuleSet]] = {
    val maybeRules = csvSectionMetadata.map(SectionRulesLoader.load(_))
    val rulesErrors = maybeRules.collect {case Xor.Left(x) => x}
    val rules = maybeRules.collect{case Xor.Right(x) => x}
    rulesErrors match {
      case Nil => Xor.right(rules)
      case _ => Xor.left(rulesErrors.foldLeft(RulesFileLoadError(""))(_ ++ _))
    }
  }

  def ==>:(facts:Facts):Xor[DecisionServiceError,RuleEngineDecision] = {
    maybeSectionRules match {
      case Xor.Right(sectionRules) =>
        RuleEngine.processRules(Rules(sectionRules),facts)
      case ee@Xor.Left(_) => ee
    }
  }
}


object DecisionServiceInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  val csvSectionMetadata = List(
    (7, 2, "/business_structure.csv", "BusinessStructure"),
    (9, 2, "/personal_service.csv", "PersonalService"),
    (2, 2, "/matrix.csv", "matrix")
  ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
}

package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, FactError, RulesFileLoadError}
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
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

//  def >>>:(questionSet:QuestionSet):Xor[DecisionServiceError,RuleEngineDecision] = {
//    val maybeDecision = for {
//      sectionRules <- maybeSectionRules
//      carryOvers <- applyToSectionRules(questionSet, sectionRules)
//    }
//    yield {
//      decision
//    }
//    maybeDecision
//  }

  def >>>:(facts:Facts):Xor[DecisionServiceError,RuleEngineDecision] = {
    maybeSectionRules match {
      case Xor.Right(sectionRules) =>
        RuleEngine.processRules(Rules(sectionRules),facts)
      case ee@Xor.Left(_) => ee
    }
  }

//  private def applyToSectionRules(questionSet:QuestionSet,sectionRules:List[SectionRuleSet]):Xor[DecisionServiceError,CarryOverFacts] = {
//    val pairs = for {
//      sectionRuleSet <- sectionRules
//      sectionFacts <- questionSet.sections.get(sectionRuleSet.section)
//    } yield {
//      (sectionRuleSet.section, SectionFactMatcher.matchFacts(sectionFacts, sectionRuleSet))
//    }
//    val rulesErrors = pairs.collect {case (a,Xor.Left(x)) => x}
//    val entries = pairs.collect {case (sectionName,Xor.Right(x)) => (sectionName,x)}
//    rulesErrors match {
//      case Nil => Xor.right(Map(entries: _*))
//      case _ => Xor.left(rulesErrors.head)
//    }
//  }

}


object DecisionServiceInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  val csvSectionMetadata = List(
    (7, 2, "/business_structure.csv", "BusinessStructure"),
    (9, 2, "/personal_service.csv", "PersonalService"),
    (2, 2, "/matrix.csv", "matrix")
  ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
}

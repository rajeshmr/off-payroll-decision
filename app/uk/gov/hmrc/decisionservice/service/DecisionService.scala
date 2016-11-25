package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, RulesFileError}
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine._


trait DecisionService {
  val ruleEngine:RuleEngine = RuleEngineInstance

  val maybeSectionRules:Xor[DecisionServiceError,List[SectionRuleSet]]

  val csvSectionMetadata:List[RulesFileMetaData]

  def loadSectionRules():Xor[DecisionServiceError,List[SectionRuleSet]] = {
    val maybeRules = csvSectionMetadata.map(RulesLoaderInstance.load(_))
    val rulesErrors = maybeRules.collect {case Xor.Left(x) => x}
    val rules = maybeRules.collect{case Xor.Right(x) => x}
    rulesErrors match {
      case Nil => Xor.right(rules)
      case _ => Xor.left(rulesErrors.foldLeft(RulesFileError(""))(_ ++ _))
    }
  }

  def ==>:(facts:Facts):Xor[DecisionServiceError,RuleEngineDecision] = {
    maybeSectionRules match {
      case Xor.Right(sectionRules) =>
        ruleEngine.processRules(Rules(sectionRules),facts)
      case e@Xor.Left(_) => e
    }
  }
}


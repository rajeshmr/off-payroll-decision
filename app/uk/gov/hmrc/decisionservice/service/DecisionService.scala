package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, FactError, RulesFileLoadError}
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine._


trait DecisionService {

  val maybeSectionRules:Xor[DecisionServiceError,List[SectionRuleSet]]

  val maybeMatrixRules:Xor[DecisionServiceError,MatrixRuleSet]

  val csvSectionMetadata:List[RulesFileMetaData]

  val csvMatrixMetadata:RulesFileMetaData

  def loadSectionRules():Xor[DecisionServiceError,List[SectionRuleSet]] = {
    val maybeRules = csvSectionMetadata.map(SectionRulesLoader.load(_))
    val rulesErrors = maybeRules.map(_.fold(e => Some(e),r => None)).flatten
    val rules = maybeRules.collect{case Xor.Right(x) => x}
    if (rulesErrors.isEmpty) Xor.right(rules) else Xor.left(rulesErrors.foldLeft(RulesFileLoadError(""))(_ ++ _))
  }

  def loadMatrixRules():Xor[DecisionServiceError,MatrixRuleSet] =
    MatrixRulesLoader.load(csvMatrixMetadata)

  def evaluate(questionSet:QuestionSet):Xor[DecisionServiceError,MatrixDecision] = {
    val maybeDecision = for {
      sectionRules <- maybeSectionRules
      matrixRules <- maybeMatrixRules
      carryOvers <- applyToSectionRules(questionSet, sectionRules)
      decision <- applyToMatrixRules(carryOvers, matrixRules)
    }
    yield {
      decision
    }
    maybeDecision
  }

  def applyToSectionRules(questionSet:QuestionSet,sectionRules:List[SectionRuleSet]):Xor[DecisionServiceError,Map[String,CarryOver]] = {
    val pairs = for {
      sectionRuleSet <- sectionRules
      sectionFacts <- questionSet.sections.get(sectionRuleSet.section)
    } yield {
      (sectionRuleSet.section, SectionFactMatcher.matchFacts(sectionFacts, sectionRuleSet))
    }
    val rulesErrors = pairs.collect {case (a,Xor.Left(x)) => x}
    val entries = pairs.collect{case (sectionName,Xor.Right(x)) => (sectionName,x)}
    if (rulesErrors.isEmpty) Xor.right(Map(entries: _*)) else Xor.left(rulesErrors.head)
  }

  def applyToMatrixRules(carryOvers:Map[String,CarryOver], matrixRules:MatrixRuleSet):Xor[DecisionServiceError,MatrixDecision] =
    MatrixFactMatcher.matchFacts(carryOvers, matrixRules)
}


object DecisionServiceInstance extends DecisionService {
  lazy val maybeSectionRules = loadSectionRules()
  lazy val maybeMatrixRules = loadMatrixRules()

  val csvSectionMetadata = List(
    (7, 2, "/business_structure.csv", "BusinessStructure"),
    (9, 2, "/personal_service.csv", "PersonalService")
  ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}

  val csvMatrixMetadata = RulesFileMetaData(2, 1, "/matrix.csv", "matrix")
}

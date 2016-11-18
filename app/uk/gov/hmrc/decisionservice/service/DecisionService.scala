package uk.gov.hmrc.decisionservice.service

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.{DecisionServiceError, RulesFileLoadError}
import uk.gov.hmrc.decisionservice.model.api.QuestionSet
import uk.gov.hmrc.decisionservice.model.rules._
import uk.gov.hmrc.decisionservice.ruleengine.{MatrixRulesLoader, RulesFileMetaData, SectionFactMatcher, SectionRulesLoader}


trait DecisionService {

  def evaluate(questionSet:QuestionSet, matrixRuleSet:MatrixRuleSet):Xor[DecisionServiceError,MatrixDecision]

}


object DecisionServiceInstance extends App /*DecisionService*/ {
  val csvSectionMetadata = List(
    (7, 2, "/business_structure.csv", "BusinessStructure"),
    (9, 2, "/personal_service.csv", "PersonalService")
  ).collect{case (q,r,f,n) => RulesFileMetaData(q,r,f,n)}
  val csvMatrixMetadata = RulesFileMetaData(2, 2, "matrix.csv", "matrix")


  def loadSectionRules():Xor[List[RulesFileLoadError],List[SectionRuleSet]] = {
    val maybeRules = csvSectionMetadata.map(SectionRulesLoader.load(_))
    val rulesErrors = maybeRules.map(_.fold(e => Some(e),r => None)).flatten
    val rules = maybeRules.collect{case Xor.Right(x) => x}
    if (rulesErrors.isEmpty) Xor.right(rules) else Xor.left(rulesErrors)
  }

  def loadMatrixRules():Xor[RulesFileLoadError,MatrixRuleSet] = {  // senza sensible :-)
    val maybeRules = MatrixRulesLoader.load(csvMatrixMetadata)
    maybeRules
  }

  def evaluate(questionSet:QuestionSet, matrixRuleSet:MatrixRuleSet):Xor[DecisionServiceError,MatrixDecision] = {

//    loadSectionRules() match {
//      case Xor.Left(e) => Xor.left(e.head) // TODO aggregate multiple errors here
//      case Xor.Right(sectionRules) =>
//        loadMatrixRules() match {
//          case Xor.Left(e) =>  Xor.left(e)
//          case Xor.Right(matrixRule) =>
//
//        }
//    }

    Xor.right(DecisionInIR35)
  }


  def applyToSectionRules(questionSet:QuestionSet,sectionRules:List[SectionRuleSet]):Xor[DecisionServiceError,List[CarryOver]] = {

    sectionRules.map { sectionRules =>
      val sectionName = sectionRules.section
      val maybeRule = questionSet.sections.get(sectionName)
      maybeRule.map { rule =>
        val result = SectionFactMatcher.matchFacts(rule, sectionRules)

      }
    }

//    }
//
//    questionSet.sections.keys.map{ sectionName =>

    Xor.left(RulesFileLoadError(""))
  }

  println(loadSectionRules())


}

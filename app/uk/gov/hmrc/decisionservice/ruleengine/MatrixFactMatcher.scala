package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._

import scala.annotation.tailrec


object MatrixFactMatcher {

  def matchMatrixFacts(matrixFacts:MatrixFacts, matrixRules:List[MatrixRule]): Xor[DecisionServiceError,MatrixDecision] =
  {
    @tailrec
    def go(facts: MatrixFacts, rules:List[MatrixRule]):Xor[DecisionServiceError,MatrixDecision] = rules match {
      case Nil => Xor.left(RulesFileError("no match found"))
      case rule :: xs =>
        if (!validateFacts(facts, rule))
          Xor.left(FactError("incorrect fact"))
        else {
          factMatches(facts, rule) match {
            case Some(result) => Xor.right(result)
            case None => go(facts, xs)
          }
        }
    }

    def validateFacts(facts: MatrixFacts, rule:MatrixRule):Boolean = facts.facts.size == rule.carryOvers.size

    def factMatches(facts: MatrixFacts, rule:MatrixRule):Option[MatrixDecision] = {
      val factAnswers = facts.facts.map(_.carryOver)
      factAnswers.zip(rule.carryOvers).filterNot(equivalent(_)) match {
        case Nil => Some(rule.decision)
        case _ => None
      }
    }

    def equivalent(p:(SectionCarryOver,SectionCarryOver)):Boolean = p match {
      case (a,b) => a == b || a.value.isEmpty || b.value.isEmpty
    }

    go(matrixFacts, matrixRules)

  }

}

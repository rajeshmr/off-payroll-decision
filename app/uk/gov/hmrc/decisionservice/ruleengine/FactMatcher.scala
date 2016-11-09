package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._

import scala.annotation.tailrec

object FactMatcher {

  def matchSectionFact(fact: Fact, rule: Rule): Xor[DecisionServiceError,SectionResult] =
  {
    @tailrec
    def go(factAnswers:List[String], ruleRows:List[RuleRow]):Xor[DecisionServiceError,SectionResult] = ruleRows match {
      case _ if factAnswers.isEmpty => Xor.left(FactError("incorrect fact"))
      case Nil => Xor.left(RulesFileError("no match found"))
      case x :: xs =>
        factMatches(factAnswers, x) match {
          case Some(result) => Xor.right(result)
          case None => go(factAnswers, xs)
        }
    }

    def factMatches(factAnswers:List[String], ruleRow:RuleRow):Option[SectionResult] = {
      factAnswers.zip(ruleRow.answers).filterNot(equivalent(_)) match {
        case Nil => Some(ruleRow.result)
        case _ => None
      }
    }

    def equivalent(p:(String,String)):Boolean = p match {
      case (a,b) => a.toLowerCase == b.toLowerCase || a.isEmpty || b.isEmpty
    }

    go(fact.interviewSection.map(_.answer), rule.rows)

  }

}

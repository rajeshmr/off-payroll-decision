package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._

object FactMatcher {

  def matchSectionFact(fact: Fact, rule: Rule): Xor[DecisionServiceError,SectionResult] =
  {
    def go(fact:List[String], rule:List[(List[String],SectionResult)]):Xor[DecisionServiceError,SectionResult] = rule match {
      case _ if fact.isEmpty => Xor.left(FactError("incorrect fact"))
      case Nil => Xor.left(RulesFileError("no match found"))
      case x :: xs =>
        factMatches(fact, x) match {
          case Some(result) => Xor.right(result)
          case None => go(fact.tail, xs)
        }
    }

    def factMatches(fact:List[String], row:(List[String],SectionResult)):Option[SectionResult] = {
      fact.zip(row._1).filterNot(equivalent(_)) match {
        case Nil => Some(row._2)
        case _ => None
      }
    }

    def equivalent(p:(String,String)):Boolean = p match {
      case (a,b) => a.toLowerCase == b.toLowerCase || a.isEmpty || b.isEmpty
    }

    go(fact.interviewSection.map(_._2), rule.answers)

  }

}

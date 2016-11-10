package uk.gov.hmrc.decisionservice.ruleengine


import cats.data.Xor
import uk.gov.hmrc.decisionservice.model._

import scala.annotation.tailrec



object SectionFactMatcher {

  def matchSectionFacts(facts: SectionFacts, rules: SectionRules): Xor[DecisionServiceError,SectionCarryOver] =
  {
    @tailrec
    def go(facts: SectionFacts, rules:List[SectionRule]):Xor[DecisionServiceError,SectionCarryOver] = rules match {
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

    def validateFacts(facts: SectionFacts, rule:SectionRule):Boolean = facts.interview.size == rule.answers.size

    def factMatches(facts: SectionFacts, rule:SectionRule):Option[SectionCarryOver] = {
      val factAnswers = facts.interview.map(_.answer)
      factAnswers.zip(rule.answers).filterNot(equivalent(_)) match {
        case Nil => Some(rule.result)
        case _ => None
      }
    }

    def equivalent(p:(String,String)):Boolean = p match {
      case (a,b) => a.toLowerCase == b.toLowerCase || a.isEmpty || b.isEmpty
    }

    go(facts, rules.rows)

  }

}

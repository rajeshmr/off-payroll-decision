package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.RulesFileError

/**
  * Created by habeeb on 11/11/2016.
  */
sealed trait RulesFileValidator {

  var possibleAnswers = List("yes", "no")
  var possibleCarryOverValues = List("low", "medium", "high")
  var possibleDecisionValues = List("in ir35", "outside ir35", "employed", "self-employed", "unknown")


  def validateRuleRow(rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit]

  def isValidSize(row: List[String], headerSize: Int): Boolean = {
    return row.size == headerSize
  }

  def isValidAnswer(answer: String, possibleValues:List[String]): Boolean = {
    return possibleValues.contains(answer.toLowerCase) || answer.equals("")
  }

  def validateResultColumnPair(resultColumnPair: List[String], rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = {

    //FIXME refactor
    val carryOverValue: String = resultColumnPair.head.trim
    val exit: String = resultColumnPair.last.trim

    if (exit.isEmpty || exit.equals("false")) {
      if (possibleCarryOverValues.contains(carryOverValue.toLowerCase) || carryOverValue.isEmpty) {
        //FIXME refactor
        return Xor.right(Unit)
      }
      else {
        return Xor.left(RulesFileError("Invalid CarryOver value for row x")) //FIXME what line has the problem???
      }
    }
    else if (exit.equals("true")) {
      if (carryOverValue.isEmpty) {
        return  Xor.right(Unit)
      }
      else {
        return  Xor.left(RulesFileError("Invalid CarryOver value for row x")) //FIXME what line has the problem???
      }
    }
    else
      return Xor.left(RulesFileError("Invalid Exit value for row x")) //FIXME what line has the problem???
  }

  def validateColumnHeaders(rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = {
    val row: List[String] = rulesFileMetaData.values ::: rulesFileMetaData.results

    if (!isValidSize(row, rulesFileMetaData.numOfAnswers + rulesFileMetaData.numOfResultColumns)) {
      return Xor.left(RulesFileError("Column header size does not match metadata"))
    }

    return Xor.right(Unit)
  }

}

object SectionRuleValidator extends RulesFileValidator {

  def validateRuleRow(rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = {
    val row: List[String] = rulesFileMetaData.values ::: rulesFileMetaData.results
    if (!isValidSize(row, rulesFileMetaData.numOfAnswers + rulesFileMetaData.numOfResultColumns)) {
      return Xor.left(RulesFileError("Column header size does not match metadata"))
    }

    for (answer <- rulesFileMetaData.values ) {
      if (!isValidAnswer(answer.trim, possibleAnswers)) {
        return Xor.left(RulesFileError("Invalid answer"))
      }
    }

    return validateResultColumnPair(rulesFileMetaData.results, rulesFileMetaData)

  }

}

object MatrixRuleValidator extends RulesFileValidator {

  def validateRuleRow(rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = {
    val row: List[String] = rulesFileMetaData.values ::: rulesFileMetaData.results
    if (!isValidSize(row, rulesFileMetaData.numOfAnswers + rulesFileMetaData.numOfResultColumns)) {
      return Xor.left(RulesFileError("Column header size does not match metadata"))
    }

    for (answer <- rulesFileMetaData.values ) {
      if (!isValidAnswer(answer.trim, possibleCarryOverValues)) {
        return Xor.left(RulesFileError("Invalid answer"))
      }
    }

    val decision: String = rulesFileMetaData.results.head
    if (!isValidAnswer(decision, possibleDecisionValues)) {
      return Xor.left(RulesFileError("Invalid Decision value on row x"))
    }
    else{
      return Xor.right(Unit)
    }

  }

}

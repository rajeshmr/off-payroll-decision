package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.RulesFileError

/**
  * Created by habeeb on 11/11/2016.
  *
  * FIXME: this class is to be refactored!!
  *
  *
  */
sealed trait RulesFileValidator {

  var possibleAnswers = List("yes", "no")
  var possibleCarryOverValues = List("low", "medium", "high")
  var possibleDecisionValues = List("in ir35", "outside ir35", "employed", "self-employed", "unknown")


  def validateRuleRow(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit]

  def isValidSize(row: List[String], headerSize: Int): Boolean = {
    return row.size == headerSize
  }

  def validateAnswer(answer: String, possibleValues:List[String], errorMessage:String): Xor[RulesFileError, Unit] = {

    if(possibleValues.contains(answer.toLowerCase) || answer.equals("")) {
      return Xor.right(Unit)
    }
    else
      return Xor.left(RulesFileError(errorMessage))
  }

  def validateResultColumnPair(resultColumnPair: List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] = {

    val carryOverValue: String = resultColumnPair.head.trim
    val exit: String = resultColumnPair.last.trim

    if (exit.isEmpty || exit.equals("false")) {
      if (possibleCarryOverValues.contains(carryOverValue.toLowerCase) || carryOverValue.isEmpty) {
        return Xor.right(Unit)
      }
      else {
        return Xor.left(RulesFileError("Invalid CarryOver value for row "+rowNumber))
      }
    }
    else if (exit.equals("true")) {
      if (carryOverValue.isEmpty) {
        return  Xor.right(Unit)
      }
      else {
        return  Xor.left(RulesFileError("Invalid CarryOver value for row "+rowNumber))
      }
    }
    else
      return Xor.left(RulesFileError("Invalid Exit value for row "+rowNumber))
  }

  def validateColumnHeaders(row: List[String], rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = {
    if (!isValidSize(row, rulesFileMetaData.numCols)) {
      return Xor.left(RulesFileError("Column header size does not match metadata"))
    }

    return Xor.right(Unit)
  }

  def validateRowSize(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int) : Xor[RulesFileError, Unit] = {
    if (!isValidSize(row, rulesFileMetaData.numCols)) {
      return Xor.left(RulesFileError("Row size does not match metadata on row "+ rowNumber))
    }
    else
      {
        return Xor.right(Unit)
      }
  }

}

object SectionRuleValidator extends RulesFileValidator {

  def validateRuleRow(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] = {
    val result: Xor[RulesFileError, Unit] = validateRowSize(row, rulesFileMetaData, rowNumber)
    if(result.isLeft){
      return result
    }

    val tuple: (List[String], List[String]) = row.splitAt(rulesFileMetaData.valueCols)

    val errorMessage: String = "Invalid answer value on row " + rowNumber
    for (answer <- tuple._1) {
      val xor: Xor[RulesFileError, Unit] = validateAnswer(answer.trim, possibleAnswers, errorMessage)
      if(xor.isLeft) return xor
    }

    return validateResultColumnPair(tuple._2, rulesFileMetaData, rowNumber)

  }

}

object MatrixRuleValidator extends RulesFileValidator {

  def validateRuleRow(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] = {
    val result: Xor[RulesFileError, Unit] = validateRowSize(row, rulesFileMetaData, rowNumber)
    if(result.isLeft){
      return result
    }

    val tuple: (List[String], List[String]) = row.splitAt(rulesFileMetaData.valueCols)

    val errorMessage: String = "Invalid CarryOver value on row " + rowNumber
    for (answer <- tuple._1) {
      val xor: Xor[RulesFileError, Unit] = validateAnswer(answer.trim, possibleCarryOverValues, errorMessage)
      if(xor.isLeft) return xor
    }

    val decision: String = tuple._2.head
    val message: String = "Invalid Decision value on row " + rowNumber

    return validateAnswer(decision, possibleDecisionValues, message)

  }

}

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

  object IsValidSize {
    def unapply(p:(List[String],Int)): Boolean = p match {
      case (row, sz) => row.size == sz
    }
  }

  def validateAnswer(answer: String, possibleValues:List[String], errorMessage:String): Xor[RulesFileError, Unit] =
    if(possibleValues.contains(answer.toLowerCase) || answer.equals(""))
      Xor.right(())
    else
      Xor.left(RulesFileError(errorMessage))

  def validateResultColumnPair(resultColumnPair: List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] = {
    val carryOverValue: String = resultColumnPair.head.trim
    val exit: String = resultColumnPair.last.trim
    if (exit.isEmpty || exit == "false") {
      if (possibleCarryOverValues.contains(carryOverValue.toLowerCase) || carryOverValue.isEmpty)
        Xor.right(())
      else
        Xor.left(RulesFileError("Invalid CarryOver value for row "+rowNumber))
    }
    else if (exit.equals("true")) {
      if (carryOverValue.isEmpty)
        Xor.right(())
      else
        Xor.left(RulesFileError("Invalid CarryOver value for row "+rowNumber))
    }
    else
      Xor.left(RulesFileError("Invalid Exit value for row "+rowNumber))
  }

  def validateColumnHeaders(row: List[String], rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = (row, rulesFileMetaData.numCols) match {
    case IsValidSize() => Xor.right(())
    case _ => Xor.left(RulesFileError("Column header size does not match metadata"))
  }

  def validateRowSize(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int) : Xor[RulesFileError, Unit] = (row, rulesFileMetaData.numCols) match {
    case IsValidSize() => Xor.right(())
    case _ => Xor.left(RulesFileError(s"Row size does not match metadata on row $rowNumber"))
  }

}

object SectionRuleValidator extends RulesFileValidator {
  def validateRuleRow(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] =
    validateRowSize(row, rulesFileMetaData, rowNumber) match {
      case r@Xor.Left(_) => r
      case Xor.Right(_) =>
        val (values, results) = row.splitAt(rulesFileMetaData.valueCols)
        val validationErrors = values.map(a => validateAnswer(a.trim, possibleAnswers, s"Invalid answer value on row $rowNumber").fold(e => Some(e),r => None)).flatten
        validationErrors.headOption.fold(validateResultColumnPair(results, rulesFileMetaData, rowNumber))(Xor.left(_))
    }
}

object MatrixRuleValidator extends RulesFileValidator {
  def validateRuleRow(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] =
    validateRowSize(row, rulesFileMetaData, rowNumber) match {
      case r@Xor.Left(_) => r
      case Xor.Right(_) =>
        val (values, results) = row.splitAt(rulesFileMetaData.valueCols)
        val validationErrors = values.map(a => validateAnswer(a.trim, possibleCarryOverValues, s"Invalid CarryOver value on row $rowNumber").fold(e => Some(e),r => None)).flatten
        validationErrors.headOption.fold(validateAnswer(results.head, possibleDecisionValues, s"Invalid Decision value on row $rowNumber"))(Xor.left(_))
    }
}

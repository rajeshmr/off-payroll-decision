package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.RulesFileError

/**
  * Created by habeeb on 11/11/2016.
  * Modified by Mimu on 25/11/2016.
  */
sealed trait RulesFileValidator {

  var allowedCarryOverValues = List("low", "medium", "high")
  var allowedValues = List("yes", "no") ::: allowedCarryOverValues
  var allowedDecisionValues = List("in ir35", "outside ir35", "employed", "self-employed", "unknown")

  object IsValidSize {
    def unapply(p:(List[String],Int)): Boolean = p match {
      case (row, sz) => row.size == sz
    }
  }

  def validateValue(value: String, possibleValues:List[String], errorMessage:String): Xor[RulesFileError, Unit] =
    if(possibleValues.contains(value.toLowerCase) || value.equals(""))
      Xor.right(())
    else
      Xor.left(RulesFileError(errorMessage))

  def validateResultCells(resultCells: List[String], rulesFileMetaData: RulesFileMetaData, row:Int): Xor[RulesFileError, Unit] =
    resultCells match {
      case Nil => Xor.left(RulesFileError(s"missing carry over in row $row"))
      case x::xs if !allowedCarryOverValues.contains(x.trim.toLowerCase) && !x.isEmpty => Xor.left(RulesFileError(s"invalid carry over value in row $row"))
      case x::Nil => Xor.right(())
      case x::exit::xs if !exit.isEmpty && exit.trim.toLowerCase() != "true" && exit.trim.toLowerCase() != "false" => Xor.left(RulesFileError(s"invalid exit value in row $row"))
      case _ => Xor.right(())
    }

  def validateColumnHeaders(row: List[String], rulesFileMetaData: RulesFileMetaData): Xor[RulesFileError, Unit] = (row, rulesFileMetaData.numCols) match {
    case IsValidSize() => Xor.right(())
    case _ => Xor.left(RulesFileError(s"column header size is ${row.size}, should be ${rulesFileMetaData.numCols}"))
  }

  def validateRowSize(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int) : Xor[RulesFileError, Unit] = (row, rulesFileMetaData.numCols) match {
    case IsValidSize() => Xor.right(())
    case _ => Xor.left(RulesFileError(s"row size does not match metadata in row $rowNumber"))
  }

  def validateRuleRow(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileError, Unit] = {
    for {
      _ <- validateRowSize(row, rulesFileMetaData, rowNumber)
      (valueCells, resultCells) = row.splitAt(rulesFileMetaData.valueCols)
      validationErrors = valueCells.map(cell => validateValue(cell.trim, allowedValues, s"invalid value in row $rowNumber")).collect { case Xor.Left(e) => e }
      _ <- Xor.fromOption(validationErrors.headOption, ()).swap
      _ <- validateResultCells(resultCells, rulesFileMetaData, rowNumber)
    }
    yield {
      ()
    }
  }

}

object SectionRuleValidator extends RulesFileValidator


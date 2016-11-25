package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.Xor
import uk.gov.hmrc.decisionservice.model.RulesFileLoadError

/**
  * Created by habeeb on 11/11/2016.
  * Modified by Mimu on 25/11/2016.
  */
sealed trait RulesFileLineValidator {

  val allowedCarryOverValues:List[String]
  val allowedValues:List[String]
  val allowedDecisionValues:List[String]

  def validateValue(value: String, errorMessage:String): Xor[RulesFileLoadError, Unit] =
    Xor.fromOption(allowedValues.find(_ == value.trim.toLowerCase).map(_ => ()),RulesFileLoadError(errorMessage))

  def validateResultCells(resultCells: List[String], rulesFileMetaData: RulesFileMetaData, row:Int): Xor[RulesFileLoadError, Unit] =
    resultCells match {
      case Nil => Xor.left(RulesFileLoadError(s"missing carry over in row $row"))
      case x::xs if !allowedCarryOverValues.contains(x.trim.toLowerCase) && !x.isEmpty => Xor.left(RulesFileLoadError(s"invalid carry over value $x in row $row in file ${rulesFileMetaData.path}"))
      case x::Nil => Xor.right(())
      case x::exit::xs if !exit.isEmpty && exit.trim.toLowerCase() != "true" && exit.trim.toLowerCase() != "false" => Xor.left(RulesFileLoadError(s"invalid exit value in row $row in file ${rulesFileMetaData.path}"))
      case _ => Xor.right(())
    }

  def validateRowSize(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int) : Xor[RulesFileLoadError, Unit] = (row, rulesFileMetaData.numCols) match {
    case _ if row.size > (rulesFileMetaData.valueCols) => Xor.right(())
    case _ => Xor.left(RulesFileLoadError(s"row size is ${row.size}, expected greater than ${rulesFileMetaData.valueCols} in row $rowNumber in file ${rulesFileMetaData.path}"))
  }

  def validateColumnHeaders(row: List[String], rulesFileMetaData: RulesFileMetaData): Xor[RulesFileLoadError, Unit] = (row, rulesFileMetaData.numCols) match {
    case _ if row.size >= rulesFileMetaData.valueCols => Xor.right(())
    case _ => Xor.left(RulesFileLoadError(s"column header size is ${row.size}, should be ${rulesFileMetaData.numCols} in file ${rulesFileMetaData.path}"))
  }

  def validateLine(row:List[String], rulesFileMetaData: RulesFileMetaData, rowNumber:Int): Xor[RulesFileLoadError, Unit] = {
    for {
      _ <- validateRowSize(row, rulesFileMetaData, rowNumber)
      (valueCells, resultCells) = row.splitAt(rulesFileMetaData.valueCols)
      validationErrors = valueCells.map(cell => validateValue(cell.trim, s"invalid value in row $rowNumber in file ${rulesFileMetaData.path}")).collect { case Xor.Left(e) => e }
      _ <- Xor.fromOption(validationErrors.headOption, ()).swap
      _ <- validateResultCells(resultCells, rulesFileMetaData, rowNumber)
    }
    yield {
      ()
    }
  }

}

object RulesFileLineValidatorInstance extends RulesFileLineValidator {
  val allowedDecisionValues = List("inir35", "outofir35", "employed", "self-employed", "unknown")
  val allowedCarryOverValues = List("low", "medium", "high") ::: allowedDecisionValues
  val allowedValues = List("yes", "no", "") ::: allowedCarryOverValues
}

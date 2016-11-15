package uk.gov.hmrc.decisionservice.tools

import java.io.File

import org.drools.decisiontable.{InputType, SpreadsheetCompiler}
import org.drools.io.ResourceFactory

object SpreadsheetDrlConverter extends App {

  val compiler = new SpreadsheetCompiler
  val drl = compiler.compile(ResourceFactory.newFileResource(new File("sheets/kb-rules-01.xls")), InputType.XLS)
  println(drl)

}

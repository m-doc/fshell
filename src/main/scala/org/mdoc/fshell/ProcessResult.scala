package org.mdoc.fshell

import scalaz.NonEmptyList

case class ProcessResult(command: NonEmptyList[String], out: String, err: String, status: Int) {

  def describe: String = s"""
    |Process "${command.list.mkString(" ")}" exited with status $status
    |  stdout: ${out.trim}
    |  stderr: ${err.trim}
  """.stripMargin.trim
}

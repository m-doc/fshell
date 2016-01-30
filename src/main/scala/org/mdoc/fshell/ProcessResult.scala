package org.mdoc.fshell

import scalaz.NonEmptyList

case class ProcessResult(command: NonEmptyList[String], out: String, err: String, status: Int)

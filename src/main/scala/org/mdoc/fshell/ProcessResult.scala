package org.mdoc.fshell

case class ProcessResult(command: List[String], out: String, err: String, status: Int)

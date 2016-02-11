package org.mdoc.fshell

import java.io.IOException

case class ProcessException(result: ProcessResult)
  extends IOException(s"""Process "${result.commandString}" exited with status ${result.status}""")

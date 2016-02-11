package org.mdoc.fshell

import java.io.IOException

case class ProcessException(result: ProcessResult) extends IOException(result.describe)

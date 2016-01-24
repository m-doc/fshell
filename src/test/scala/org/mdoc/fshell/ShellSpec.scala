package org.mdoc.fshell

import java.nio.file.Files
import org.mdoc.fshell.Shell.ShellSyntax
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scala.util.Random

class ShellSpec extends Properties("Shell") {

  property("createTempFile Task") = secure {
    val (prefix, suffix) = (Random.nextString(8), Random.nextString(8))
    val path = Shell.createTempFile(prefix, suffix).runTask.run
    val name = path.getFileName.toString
    val result = name.startsWith(prefix) && name.endsWith(suffix) && Files.exists(path)
    Files.delete(path)
    result
  }
}

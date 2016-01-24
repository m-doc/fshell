package org.mdoc.fshell

import org.mdoc.fshell.Shell.ShellSyntax
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scala.util.Random

class ShellSpec extends Properties("Shell") {

  property("createTempFile Task") = secure {
    val (prefix, suffix) = (Random.nextString(8), Random.nextString(8))
    val p = for {
      path <- Shell.createTempFile(prefix, suffix)
      name = path.getFileName.toString
      created <- Shell.fileExists(path)
      _ <- Shell.delete(path)
    } yield created && name.startsWith(prefix) && name.endsWith(suffix)
    p.runTask.run
  }

  property("delete, fileExists Task") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      created <- Shell.fileExists(path)
      _ <- Shell.delete(path)
      deleted <- Shell.fileNotExists(path)
    } yield created && deleted
    p.runTask.run
  }
}

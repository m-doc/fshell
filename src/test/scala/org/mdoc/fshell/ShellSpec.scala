package org.mdoc.fshell

import java.io.IOException
import java.nio.file.Paths
import org.mdoc.fshell.Shell.ShellSyntax
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scala.util.Random

class ShellSpec extends Properties("Shell") {

  property("createParentDirectories") = secure {
    val p = for {
      dir <- Shell.createParentDirectories(Paths.get("./file"))
      created <- Shell.isDirectory(dir)
    } yield created
    p.runTask.run
  }

  property("createTempFile") = secure {
    val (prefix, suffix) = (Random.nextString(8), Random.nextString(8))
    val p = for {
      path <- Shell.createTempFile(prefix, suffix)
      name = path.getFileName.toString
      created <- Shell.fileExists(path)
      _ <- Shell.delete(path)
    } yield created && name.startsWith(prefix) && name.endsWith(suffix)
    p.runTask.run
  }

  property("delete, fileExists") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      created <- Shell.fileExists(path)
      _ <- Shell.delete(path)
      deleted <- Shell.fileNotExists(path)
    } yield created && deleted
    p.runTask.run
  }

  property("isDirectory") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      notDir <- Shell.isDirectory(path)
      isDir <- Shell.isDirectory(path.getParent)
      _ <- Shell.delete(path)
    } yield !notDir && isDir
    p.runTask.run
  }

  property("readAllBytes empty file") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      bytes <- Shell.readAllBytes(path)
      _ <- Shell.delete(path)
    } yield bytes.isEmpty
    p.runTask.run
  }

  property("readProcess") = secure {
    val p = Shell.readProcess("echo", List("hello", "world")).map(_.out.trim)
    p.runTask.run ?= "hello world"
  }

  property("readProcess nonexistent command") = secure {
    val p = Shell.readProcess("this-command-does-not-exists", List.empty)
    p.runTask.attemptRun.fold(_.isInstanceOf[IOException], _ => false)
  }

  property("readProcessIn, createDirectory") = secure {
    val name = "test" + Random.nextInt().abs
    val p = for {
      dir <- Shell.createDirectory(Paths.get("./" + name))
      res <- Shell.readProcessIn("pwd", List.empty, dir)
      _ <- Shell.delete(dir)
    } yield res.out.contains(name)
    p.runTask.run
  }
}

package org.mdoc.fshell

import java.io.IOException
import java.nio.file.Paths
import org.mdoc.fshell.Shell.ShellSyntax
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scala.util.Random
import scalaz.NonEmptyList

class ShellSpec extends Properties("Shell") {

  property("createParentDirectories") = secure {
    val p = for {
      dir <- Shell.createParentDirectories(Paths.get("./file"))
      created <- Shell.isDirectory(dir)
    } yield created
    p.yolo
  }

  property("createTempFile") = secure {
    val (prefix, suffix) = (Random.nextString(8), Random.nextString(8))
    val p = for {
      path <- Shell.createTempFile(prefix, suffix)
      name = path.getFileName.toString
      created <- Shell.fileExists(path)
      _ <- Shell.delete(path)
    } yield created && name.startsWith(prefix) && name.endsWith(suffix)
    p.yolo
  }

  property("delete, fileExists") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      created <- Shell.fileExists(path)
      _ <- Shell.delete(path)
      deleted <- Shell.fileNotExists(path)
    } yield created && deleted
    p.yolo
  }

  property("isDirectory") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      notDir <- Shell.isDirectory(path)
      isDir <- Shell.isDirectory(path.getParent)
      _ <- Shell.delete(path)
    } yield !notDir && isDir
    p.yolo
  }

  property("readAllBytes empty file") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      bytes <- Shell.readAllBytes(path)
      _ <- Shell.delete(path)
    } yield bytes.isEmpty
    p.yolo
  }

  property("readProcess") = secure {
    val p = Shell.readProcess(NonEmptyList("echo", "hello", "world")).map(_.out.trim)
    p.yolo ?= "hello world"
  }

  property("readProcess nonexistent command") = secure {
    val p = Shell.readProcess(NonEmptyList("this-command-does-not-exists"))
    p.runTask.attemptRun.fold(_.isInstanceOf[IOException], _ => false)
  }

  property("readProcess non-zero status") = secure {
    val p = Shell.readProcess(NonEmptyList("sh", "this-command-does-not-exists"))
    p.map(_.status).yolo != 0 && throws(classOf[IOException])(p.throwOnError.yolo)
  }

  property("readProcessIn, createDirectory") = secure {
    val name = "test" + math.abs(Random.nextInt())
    val p = for {
      dir <- Shell.createDirectory(Paths.get("./" + name))
      res <- Shell.readProcessIn(NonEmptyList("pwd"), dir)
      _ <- Shell.delete(dir)
    } yield res.out.contains(name)
    p.yolo
  }
}

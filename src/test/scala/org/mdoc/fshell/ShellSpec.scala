package org.mdoc.fshell

import java.io.IOException
import java.nio.file.Paths
import java.util
import java.util.UUID
import org.mdoc.fshell.Shell.ShellSyntax
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scala.util.Random
import scalaz.NonEmptyList
import scodec.bits.ByteVector

object ShellSpec extends Properties("Shell") {

  property("createParentDirectories") = secure {
    val p = for {
      dir <- Shell.createParentDirectories(Paths.get("./file"))
      created <- Shell.isDirectory(dir)
    } yield created
    p.yolo
  }

  property("createTempDirectory") = secure {
    val prefix = Random.nextString(8)
    val p = for {
      path <- Shell.createTempDirectory(prefix)
      created <- Shell.isDirectory(path)
      _ <- Shell.delete(path)
      deleted <- Shell.isDirectory(path).map(!_)
    } yield created && deleted
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

  property("deleteAll") = secure {
    val p = for {
      path1 <- Shell.createTempFile("", "")
      path2 <- Shell.createTempFile("", "")
      _ <- Shell.deleteAll(List(path1, path2))
      deleted1 <- Shell.fileNotExists(path1)
      deleted2 <- Shell.fileNotExists(path2)
    } yield deleted1 && deleted2
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

  property("readAllBytesIfFileExists empty file") = secure {
    val p = for {
      path <- Shell.createTempFile("", "")
      maybeBytes <- Shell.readAllBytesIfFileExists(path)
      _ <- Shell.delete(path)
    } yield maybeBytes.isDefined
    p.yolo
  }

  property("readAllBytesIfFileExists non-existing file") = secure {
    val p = for {
      maybeBytes <- Shell.readAllBytesIfFileExists(Paths.get("asdf"))
    } yield maybeBytes.isEmpty
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
    p.map(_.status).yolo != 0 && throws(classOf[ProcessException])(p.throwOnError.yolo)
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

  property("writeToTempFile") = secure {
    val bytes = ByteVector.view(Random.nextString(8).getBytes)
    val p = for {
      path <- Shell.writeToTempFile("", "", bytes)
      read <- Shell.readAllBytes(path)
      _ <- Shell.delete(path)
    } yield read
    p.yolo ?= bytes
  }

  property("filesInDirectory unfiltered") = secure {
    val p = for {
      path1 <- Shell.createTempFile(Random.nextString(5), "." + Random.nextString(3))
      path2 <- Shell.createTempFile(Random.nextString(5), "." + Random.nextString(3))
      found <- Shell.filesInDirectory(path1.getParent, None)
      _ <- Shell.deleteAll(List(path1, path2))
    } yield found.contains(path1) && found.contains(path2)
    p.yolo
  }

  property("filesInDirectory filtered") = secure {
    val suffix1 = Random.nextString(3)
    val suffix2 = Random.nextString(3)
    val p = for {
      path1 <- Shell.createTempFile(Random.nextString(5), "." + suffix1)
      path2 <- Shell.createTempFile(Random.nextString(5), "." + suffix2)
      path3 <- Shell.createTempFile(Random.nextString(5), "." + Random.nextString(3))
      found <- Shell.filesInDirectory(path1.getParent, Some(NonEmptyList(suffix1, suffix2)))
      _ <- Shell.deleteAll(List(path1, path2, path3))
    } yield found.contains(path1) && found.contains(path2) && !found.contains(path3)
    p.yolo
  }
}

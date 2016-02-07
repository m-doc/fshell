package org.mdoc.fshell

import java.nio.file.{ Files, Path }
import scala.sys.process.{ Process, ProcessLogger }
import scalaz.{ ~>, Coyoneda, NonEmptyList }
import scalaz.concurrent.Task
import scodec.bits.ByteVector

sealed trait ShellOp[T]

object ShellOp {
  type FreeFunctor[T] = Coyoneda[ShellOp, T]

  case class CreateDirectory(dir: Path) extends ShellOp[Path]
  case class CreateDirectories(dir: Path) extends ShellOp[Path]
  case class CreateTempDirectory(prefix: String) extends ShellOp[Path]
  case class CreateTempFile(prefix: String, suffix: String) extends ShellOp[Path]
  case class Delete(path: Path) extends ShellOp[Unit]
  case class FileExists(path: Path) extends ShellOp[Boolean]
  case class IsDirectory(path: Path) extends ShellOp[Boolean]
  case class ReadAllBytes(path: Path) extends ShellOp[ByteVector]
  case class ReadProcess(command: NonEmptyList[String], workingDir: Option[Path]) extends ShellOp[ProcessResult]
  case class Write(path: Path, bytes: ByteVector) extends ShellOp[Unit]

  val shellOpToTask: ShellOp ~> Task =
    new (ShellOp ~> Task) {
      override def apply[T](op: ShellOp[T]): Task[T] =
        op match {
          case CreateDirectory(dir) =>
            Task.delay(Files.createDirectory(dir))

          case CreateDirectories(dir) =>
            Task.delay(Files.createDirectories(dir))

          case CreateTempDirectory(prefix) =>
            Task.delay(Files.createTempDirectory(prefix))

          case CreateTempFile(prefix, suffix) =>
            Task.delay(Files.createTempFile(prefix, suffix))

          case Delete(path) =>
            Task.delay(Files.delete(path))

          case FileExists(path) =>
            Task.delay(Files.exists(path))

          case IsDirectory(path) =>
            Task.delay(Files.isDirectory(path))

          case ReadAllBytes(path) =>
            Task.delay(ByteVector.view(Files.readAllBytes(path)))

          case ReadProcess(command, dir) => Task.delay {
            def appendTo(sb: StringBuilder)(line: String): Unit = {
              sb.append(line)
              sb.append(System.lineSeparator())
              ()
            }

            val outBuf = new StringBuilder
            val errBuf = new StringBuilder
            val logger = ProcessLogger(appendTo(outBuf), appendTo(errBuf))

            val status = Process(command.list, dir.map(_.toFile)).run(logger).exitValue()
            ProcessResult(command, outBuf.result(), errBuf.result(), status)
          }

          case Write(path, bytes) =>
            Task.delay { Files.write(path, bytes.toArray); () }
        }
    }
}

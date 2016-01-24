package org.mdoc.fshell

import java.nio.file.{ Files, Path }
import scalaz.{ ~>, Coyoneda }
import scalaz.concurrent.Task

sealed trait ShellOp[T]

object ShellOp {
  type FreeFunctor[T] = Coyoneda[ShellOp, T]

  case class CreateTempFile(prefix: String, suffix: String) extends ShellOp[Path]
  case class Delete(path: Path) extends ShellOp[Unit]
  case class FileExists(path: Path) extends ShellOp[Boolean]

  val shellOpToTask: ShellOp ~> Task =
    new (ShellOp ~> Task) {
      override def apply[T](op: ShellOp[T]): Task[T] =
        op match {
          case CreateTempFile(prefix, suffix) =>
            Task.delay(Files.createTempFile(prefix, suffix))
          case Delete(path) =>
            Task.delay(Files.delete(path))
          case FileExists(path) =>
            Task.delay(Files.exists(path))
        }
    }
}

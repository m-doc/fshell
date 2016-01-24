package org.mdoc.fshell

import java.nio.file.{ Files, Path }
import scalaz.{ ~>, Coyoneda }
import scalaz.concurrent.Task

sealed trait ShellOp[T]

object ShellOp {
  type ShellResult[T] = ShellOp[Result[T]]
  type FreeFunctor[T] = Coyoneda[ShellResult, T]

  case class CreateTempFile(prefix: String, suffix: String) extends ShellResult[Path]
  case class Delete(path: Path) extends ShellResult[Unit]

  val shellOpToTask: ShellResult ~> Task =
    new (ShellResult ~> Task) {
      override def apply[A](fa: ShellResult[A]): Task[A] =
        fa match {
          case CreateTempFile(prefix, suffix) =>
            Task.delay(Files.createTempFile(prefix, suffix))
          case Delete(path) =>
            Task.delay(Files.delete(path))
        }
    }
}

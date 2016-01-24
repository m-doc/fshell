package org.mdoc.fshell

import java.nio.file.Path
import scalaz.{ Monad, Free }
import scalaz.concurrent.Task

object Shell {

  // constructors

  def createTempFile(prefix: String, suffix: String): Shell[Path] =
    Free.liftFC(ShellOp.CreateTempFile(prefix, suffix))

  def delete(path: Path): Shell[Unit] =
    Free.liftFC(ShellOp.Delete(path))

  // syntax

  implicit class ShellSyntax[T](val self: Shell[T]) extends AnyVal {
    def runTask: Task[T] =
      Free.runFC(self)(ShellOp.shellOpToTask)
  }

  // instances

  implicit val shellMonad: Monad[Shell] =
    Free.freeMonad[ShellOp.FreeFunctor]
}

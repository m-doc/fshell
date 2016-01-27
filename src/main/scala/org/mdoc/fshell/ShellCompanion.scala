package org.mdoc.fshell

import java.nio.file.Path
import scalaz.{ Free, Monad }
import scalaz.concurrent.Task
import scodec.bits.ByteVector

object ShellCompanion {

  // constructors

  def createDirectory(dir: Path): Shell[Path] =
    Free.liftFC(ShellOp.CreateDirectory(dir))

  def createDirectories(dir: Path): Shell[Path] =
    Free.liftFC(ShellOp.CreateDirectories(dir))

  def createTempFile(prefix: String, suffix: String): Shell[Path] =
    Free.liftFC(ShellOp.CreateTempFile(prefix, suffix))

  def delete(path: Path): Shell[Unit] =
    Free.liftFC(ShellOp.Delete(path))

  def fileExists(path: Path): Shell[Boolean] =
    Free.liftFC(ShellOp.FileExists(path))

  def isDirectory(path: Path): Shell[Boolean] =
    Free.liftFC(ShellOp.IsDirectory(path))

  def readAllBytes(path: Path): Shell[ByteVector] =
    Free.liftFC(ShellOp.ReadAllBytes(path))

  def readProcess(command: String, args: List[String]): Shell[ProcessResult] =
    Free.liftFC(ShellOp.ReadProcess(command, args, None))

  def readProcessIn(command: String, args: List[String], workingDir: Path): Shell[ProcessResult] =
    Free.liftFC(ShellOp.ReadProcess(command, args, Some(workingDir)))

  // derived operations

  def createParentDirectories(path: Path): Shell[Path] =
    createDirectories(path.getParent)

  def fileNotExists(path: Path): Shell[Boolean] =
    fileExists(path).map(!_)

  // syntax

  implicit class ShellSyntax[T](val self: Shell[T]) extends AnyVal {
    def runTask: Task[T] =
      Free.runFC(self)(ShellOp.shellOpToTask)

    def yolo: T =
      runTask.run
  }

  // instances

  implicit val shellMonad: Monad[Shell] =
    Free.freeMonad[ShellOp.FreeFunctor]
}

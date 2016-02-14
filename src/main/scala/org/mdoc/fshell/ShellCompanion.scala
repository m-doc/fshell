package org.mdoc.fshell

import java.nio.file.Path
import scalaz.{ Free, Monad, NonEmptyList }
import scalaz.concurrent.Task
import scalaz.std.list._
import scalaz.syntax.traverse._
import scodec.bits.ByteVector

object ShellCompanion {

  // constructors

  def createDirectory(dir: Path): Shell[Path] =
    Free.liftFC(ShellOp.CreateDirectory(dir))

  def createDirectories(dir: Path): Shell[Path] =
    Free.liftFC(ShellOp.CreateDirectories(dir))

  def createTempDirectory(prefix: String): Shell[Path] =
    Free.liftFC(ShellOp.CreateTempDirectory(prefix))

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

  def readProcess(command: NonEmptyList[String]): Shell[ProcessResult] =
    Free.liftFC(ShellOp.ReadProcess(command, None))

  def readProcessIn(command: NonEmptyList[String], workingDir: Path): Shell[ProcessResult] =
    Free.liftFC(ShellOp.ReadProcess(command, Some(workingDir)))

  def write(path: Path, bytes: ByteVector): Shell[Unit] =
    Free.liftFC(ShellOp.Write(path, bytes))

  // derived operations

  def createParentDirectories(path: Path): Shell[Path] =
    createDirectories(path.getParent)

  def deleteAll(paths: List[Path]): Shell[Unit] =
    paths.map(delete).sequence_

  def fileNotExists(path: Path): Shell[Boolean] =
    fileExists(path).map(!_)

  def writeToTempFile(prefix: String, suffix: String, bytes: ByteVector): Shell[Path] =
    createTempFile(prefix, suffix).flatMap(path => write(path, bytes).map(_ => path))

  // syntax

  implicit class ShellSyntax[T](val self: Shell[T]) extends AnyVal {
    def runTask: Task[T] =
      Free.runFC(self)(ShellOp.shellOpToTask)

    def throwOnError(implicit ev: T =:= ProcessResult): Shell[T] =
      self.map { res => if (res.status != 0) throw ProcessException(res) else res }

    def yolo: T =
      runTask.run
  }

  // instances

  implicit val shellMonad: Monad[Shell] =
    Free.freeMonad[ShellOp.FreeFunctor]
}

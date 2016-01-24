package org.mdoc.fshell

sealed trait Result[T]

object Result {
  case class Success[T](value: T) extends Result[T]
  case class Failure[T](exception: Exception) extends Result[T]
}

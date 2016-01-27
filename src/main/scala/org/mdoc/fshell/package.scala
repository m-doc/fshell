package org.mdoc

import scalaz.Free

package object fshell {
  type Shell[T] = Free[ShellOp.FreeFunctor, T]

  // workaround for https://issues.scala-lang.org/browse/SI-7139
  val Shell: ShellCompanion.type = ShellCompanion
}

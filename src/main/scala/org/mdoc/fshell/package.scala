package org.mdoc

import scalaz.Free

package object fshell {
  type Shell[T] = Free[ShellOp.FreeFunctor, T]
}

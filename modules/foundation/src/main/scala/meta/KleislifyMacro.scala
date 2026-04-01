package foundation
package meta

import cats.data.*

trait KleisliApply[T] {
  def apply[F[_]]: F[T]
}

object KleisliApply {
  inline def derived[T]: KleisliApply[T] = new KleisliApply[T] {
    override def apply[F[_]]: F[T] = ???
  }
}

package ge.zgharbi.ganbari.core
package id

import cats.effect.Sync
import cats.syntax.all.*

trait IdService[F[_]] {
  def make[A]: F[IdType[A]]

  def from[A](s: String): F[IdType[A]]
}

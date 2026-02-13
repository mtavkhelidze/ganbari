package ge.zgharbi.ganbari.core
package id

import cats.effect.Sync
import cats.syntax.all.*

import java.util.UUID

object IdManager{
  def apply[F[_]: Sync]: IdService[F] = new IdService[F] {
    override def make[A]: F[IdType[A]] =
      Sync[F].delay(UUID.randomUUID()).map(IdType.apply)

    override def from[A](s: String): F[IdType[A]] =
      Sync[F].delay(UUID.fromString(s)).map(IdType.apply)
  }
}

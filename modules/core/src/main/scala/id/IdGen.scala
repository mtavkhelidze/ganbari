package ge.zgharbi.ganbari.core
package id

import cats.effect.*

import java.util.UUID

trait IdGen[F[_], Raw] {
  def make: F[Raw]
  def from(s: String): F[Raw]
}

object IdGen {
  def uuid[F[_]: Sync]: IdGen[F, UUID] = new IdGen[F, UUID] {

    override def make: F[UUID] = Sync[F].pure(UUID.randomUUID())

    override def from(s: String): F[UUID] =
      Sync[F].catchNonFatal(UUID.fromString(s))
  }
}

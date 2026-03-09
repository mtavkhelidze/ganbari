package ge.zgharbi.ganbari.services

import cats.effect.*

import java.util.UUID

trait IdGen[F[_], Raw] {
  def read(s: String): F[Raw]
  def write: F[Raw]
}

object IdGen {
  def uuid[F[_]: Sync]: IdGen[F, UUID] = new IdGen[F, UUID] {
    override def read(s: String): F[UUID] =
      Sync[F].catchNonFatal(UUID.fromString(s))

    override def write: F[UUID] =
      Sync[F].delay(UUID.randomUUID())
  }
}

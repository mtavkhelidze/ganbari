package fuda

import cats.*
import cats.effect.*

import java.util.UUID

given defaultMaker[F[_]: ApplicativeThrow]: UuidMaker[F] with {
  def make: F[UUID] =
    ApplicativeThrow[F].catchNonFatal(UUID.randomUUID())
}
given defaultReader[F[_]: ApplicativeThrow]: UuidReader[F] with {
  def read(s: String): F[UUID] =
    ApplicativeThrow[F].catchNonFatal(UUID.fromString(s))
}

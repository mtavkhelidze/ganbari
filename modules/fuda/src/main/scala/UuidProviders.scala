package fuda

import cats.*

import java.util.UUID

trait UuidMaker[F[_]] {
  def make: F[UUID]
}
trait UuidReader[F[_]] {
  def read(s: String): F[UUID]
}

object UuidMaker {
  given [F[_]: ApplicativeThrow]: UuidMaker[F] with {
    def make: F[UUID] =
      ApplicativeThrow[F].catchNonFatal(UUID.randomUUID())
  }
}

object UuidReader {
  given [F[_]: ApplicativeThrow]: UuidReader[F] with {
    def read(s: String): F[UUID] =
      ApplicativeThrow[F].catchNonFatal(UUID.fromString(s))
  }
}

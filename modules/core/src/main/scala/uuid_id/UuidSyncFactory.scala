package ge.zgharbi.ganbari.core
package uuid_id

import cats.effect.Sync
import cats.syntax.all.*
import cats.{Eq, Show}

import java.util.UUID

opaque type UuidType[A] = UUID

trait IsUUID[A] {
  def fromUUID(uuid: UUID): A
  def toUUID(id: A): UUID
}

object UuidType {
  def apply[A](uuid: UUID)(using iso: IsUUID[A]): A =
    iso.fromUUID(uuid)

  extension [A](id: A)(using iso: IsUUID[A]) def value: UUID = iso.toUUID(id)

  given [A](using IsUUID[A]): Eq[A] = Eq.by(_.value)

  given [A](using IsUUID[A]): Show[A] = Show.show(id => id.value.toString)

  given [A](using IsUUID[A]): Ordering[A] = Ordering.by(_.value)

  given [A]: IsUUID[UuidType[A]] with {
    def fromUUID(uuid: UUID): UuidType[A] = uuid
    def toUUID(id: UuidType[A]): UUID = id
  }
}

trait UuidFactory[F[_]] {
  def make[A](using IsUUID[A]): F[A]

  def from[A](s: String)(using IsUUID[A]): F[A]
}

object UuidSyncFactory {
  def apply[F[_]: Sync]: UuidFactory[F] = new UuidFactory[F] {
    override def make[A](using iso: IsUUID[A]): F[A] =
      Sync[F].delay(UUID.randomUUID()).map(iso.fromUUID)

    override def from[A](s: String)(using iso: IsUUID[A]): F[A] =
      Sync[F]
        .delay(UUID.fromString(s))
        .map(iso.fromUUID)
        .adaptError { case e: IllegalArgumentException =>
          IllegalArgumentException(
            s"ID path corrupted: '$s' is not a valid UUID",
            e,
          )
        }
  }
}

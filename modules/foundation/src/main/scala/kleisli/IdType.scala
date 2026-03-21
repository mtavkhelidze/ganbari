package kleisli

import base.Iso
import cats.*
import cats.data.Kleisli
import cats.effect.*
import cats.implicits.*
import services.IdService

import java.util.UUID

opaque type IdType[A] = Any

trait IdFactory[F[_], Raw](private val gen: IdService[F, Raw]) {
  def make[A](using iso: Iso[UUID, A]): Kleisli[F, Unit, IdType[A]]
  def read[A](using iso: Iso[UUID, A]): Kleisli[F, String, IdType[A]]
}

object IdType {
  def uuid[F[_]: Sync](gen: IdService[F, UUID]) = new IdFactory[F, UUID](gen) {

    override def make[A](using iso: Iso[UUID, A]): Kleisli[F, Unit, IdType[A]] =
      gen.write >>> Kleisli.pure(iso.from(_))

    override def read[A](using
        iso: Iso[UUID, A],
    ): Kleisli[F, String, IdType[A]] =
      gen.read >>> Kleisli.pure(iso.from(_))
  }

  given [A]: Eq[IdType[A]] = (a, b) => a == b

  given [A]: Iso[UUID, IdType[A]] with {
    def from(a: UUID): IdType[A] = a.asInstanceOf[A]

    def to(a: IdType[A]): UUID = a.asInstanceOf[UUID]
  }

  given [A]: Iso[Long, IdType[A]] with {
    def from(a: Long): IdType[A] = a.asInstanceOf[A]
    def to(a: IdType[A]): Long = a.asInstanceOf[Long]
  }
}

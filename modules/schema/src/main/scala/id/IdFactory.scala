package schema
package id

import cats.*
import cats.effect.Sync
import cats.syntax.all.*

import java.util.UUID

trait IdFactory[F[_], Raw] {
  def make[A](using IdIso[Raw, A]): F[A]
  def from[A](s: String)(using IdIso[Raw, A]): F[A]
}

object IdFactory {
  def uuid[F[_]: Sync] = new IdFactory[F, UUID] {
    val gen: IdGen[F, UUID] = IdGen.uuid[F]

    override def make[A](using iso: IdIso[UUID, A]): F[A] =
      gen.make.map(iso.from)

    override def from[A](s: String)(using iso: IdIso[UUID, A]): F[A] =
      gen.from(s).map(iso.from)
  }
}

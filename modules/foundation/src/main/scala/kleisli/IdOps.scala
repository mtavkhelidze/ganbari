package kleisli

import base.IdIso
import cats.effect.Sync
import cats.syntax.all.*
import ge.zgharbi.ganbari.services.IdGen

import java.util.UUID

trait IdFactory[F[_], Raw] {
  def make[A](using IdIso[Raw, A]): F[A]
  def read[A](s: String)(using IdIso[Raw, A]): F[A]
}

object IdOps {
  def uuid[F[_]: Sync] = new IdFactory[F, UUID] {
    val gen: IdGen[F, UUID] = IdGen.uuid[F]

    override def make[A](using iso: IdIso[UUID, A]): F[A] =
      gen.write.map(iso.from)

    override def read[A](s: String)(using iso: IdIso[UUID, A]): F[A] =
      gen.read(s).map(iso.from)
  }
}

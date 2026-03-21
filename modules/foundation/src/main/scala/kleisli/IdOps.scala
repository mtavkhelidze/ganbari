package kleisli

import cats.effect.Sync
import cats.syntax.all.*
import java.util.UUID
import services.IdService

trait IdFactory[F[_], Raw] {
  def make[A](using IdIso[Raw, A]): F[A]
  def read[A](s: String)(using IdIso[Raw, A]): F[A]
}

object IdOps {
  def uuid[F[_]: Sync] = new IdFactory[F, UUID] {
    val gen: IdService[F, UUID] = IdService.uuid[F]

    override def make[A](using iso: IdIso[UUID, A]): F[A] =
      gen.write.map(iso.from)

    override def read[A](s: String)(using iso: IdIso[UUID, A]): F[A] =
      gen.read(s).map(iso.from)
  }
}

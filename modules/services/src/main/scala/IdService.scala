package services

import cats.data.Kleisli
import cats.effect.*

import java.util.UUID

trait IdService[F[_], Raw] {
  def read: Kleisli[F, String, Raw]
  def write: Kleisli[F, Unit, Raw]
}

object IdService {
  def uuid[F[_]: Sync]: IdService[F, UUID] = new IdService[F, UUID] {
    override def read: Kleisli[F, String, UUID] =
      Kleisli(s => Sync[F].catchNonFatal(UUID.fromString(s)))

    override def write: Kleisli[F, Unit, UUID] =
      Kleisli.liftF(Sync[F].delay(UUID.randomUUID()))
  }
}

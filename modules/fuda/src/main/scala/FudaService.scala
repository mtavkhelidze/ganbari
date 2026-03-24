package fuda

import cats.data.Kleisli
import cats.effect.*

import java.util.UUID
import scala.annotation.implicitNotFound

@implicitNotFound(
  "Cannot find FudaService[${F}, ${Raw}]. " +
    "Make sure it exists and in scope.",
)
private[fuda] trait FudaService[F[_], Raw] {
  def read: Kleisli[F, String, Raw]
  def write: Kleisli[F, Unit, Raw]
}

private[fuda] object FudaService {
  given [F[_]: Sync]: FudaService[F, UUID] = new FudaService[F, UUID] {
    override def read: Kleisli[F, String, UUID] =
      Kleisli(s => Sync[F].catchNonFatal(UUID.fromString(s)))

    override def write: Kleisli[F, Unit, UUID] =
      Kleisli.liftF(Sync[F].pure(UUID.randomUUID()))
  }
}

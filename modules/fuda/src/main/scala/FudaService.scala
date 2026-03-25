package fuda

import cats.data.Kleisli
import cats.effect.*

import java.util.UUID
import scala.annotation.implicitNotFound

import FudaService.given

@implicitNotFound(
  "Cannot find FudaService[${F}, ${Raw}]. " +
    "Make sure it exists and in scope.",
)
trait FudaService[F[_], Raw] {
  def read: Kleisli[F, String, Raw]
  def write: Kleisli[F, Unit, Raw]
}

object FudaService {
  given [F[_]: Sync]: FudaService[F, UUID] = new FudaService[F, UUID] {
    override def read: Kleisli[F, String, UUID] =
      Kleisli(s => Sync[F].catchNonFatal(UUID.fromString(s)))

    override def write: Kleisli[F, Unit, UUID] =
      Kleisli.liftF(Sync[F].pure(UUID.randomUUID()))
  }
//
//  given [F[_]: Sync]: FudaService[F, Long] = new FudaService[F, Long] {
//    override def read: Kleisli[F, String, Long] = ???
//
//    override def write: Kleisli[F, Unit, Long] = ???
//  }
}

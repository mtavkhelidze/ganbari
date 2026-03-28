package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import fuda.*

import java.util.UUID
import scala.annotation.targetName

// delcaration
opaque type DomainId = UuidFuda
object DomainId extends Fuda[DomainId]

case class Domain(
    id: DomainId,
    name: String,
)

given [F[_]: ApplicativeThrow]: FudaWriter[F, UUID] =
  Kleisli(_ => ApplicativeThrow[F].pure(UUID.randomUUID()))

given [F[_]: ApplicativeThrow]: FudaReader[F, UUID] =
  Kleisli(s => ApplicativeThrow[F].catchNonFatal(UUID.fromString(s)))

object Domain {
  def apply[F[_]: ApplicativeThrow]: Kleisli[F, String, Domain] = {
    (DomainId.create[F].local(_ => ()), Kleisli.ask[F, String])
      .mapN(Domain(_, _))
  }

  @targetName("restorative")
  def apply[F[_]: ApplicativeThrow]: Kleisli[F, (String, String), Domain] =
    (
      DomainId.restore[F].local[(String, String)](_._1),
      Kleisli.ask[F, (String, String)].map(_._2),
    ).mapN(Domain(_, _))

  private def apply(id: DomainId, name: String): Domain = new Domain(id, name)
}

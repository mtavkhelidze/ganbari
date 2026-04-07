package foundation
package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import fuda.*

opaque type DomainId <: Fuda.Id = Fuda.Id

case class Domain private (
    id: DomainId,
    name: OuString,
)

object Domain {
  def apply[F[_]: MonadThrow]: Kleisli[F, String, Domain] = {
    Kleisli
      .pure(apply.curried)
      .ap(Fuda[DomainId].make[F].lmap(_ => ()))
      .ap(Kleisli.ask[F, String].andThen(OuString[F].apply))
  }
}

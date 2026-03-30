package foundation
package kleisli

import kenshou.*

import cats.*
import cats.data.*
import cats.implicits.*
import fuda.*
import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*

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

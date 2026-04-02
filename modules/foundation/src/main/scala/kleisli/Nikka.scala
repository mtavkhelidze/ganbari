package foundation
package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import fuda.*

opaque type NikkaId <: Fuda.Id = Fuda.Id

case class Nikka private (
    id: NikkaId,
    name: OuString,
    schedule: Schedule,
    domain: Domain,
)

object Nikka {
  type Input =
    (name: String, schedule: Schedule, domain: Domain)

  def apply[F[_]: MonadThrow]: Kleisli[F, Input, Nikka] = {
    Kleisli
      .pure(apply.curried)
      .ap(Fuda[NikkaId].make[F].lmap(_ => ()))
      .ap(OuString.apply[F].lmap[Input](_.name))
      .ap(Kleisli.ask[F, Input].map(_.schedule))
      .ap(Kleisli.ask[F, Input].map(_.domain))
  }
}

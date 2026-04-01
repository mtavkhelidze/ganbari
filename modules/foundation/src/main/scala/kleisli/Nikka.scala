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

  def apply[F[_]: MonadThrow]: Kleisli[F, Input, Nikka] =
    (
      Fuda[NikkaId].make[F].local(_ => ()),
      OuString.apply[F].local[Input](_.name),
      Kleisli.ask[F, Input].map(_.schedule),
      Kleisli.ask[F, Input].map(_.domain),
    ).mapN(new Nikka(_, _, _, _))
}

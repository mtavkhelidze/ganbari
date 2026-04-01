package foundation
package kleisli

import kleisli.Nichiji

import cats.effect.Sync
import fuda.*

opaque type NikkaId <: Fuda.Id = Fuda.Id

sealed case class Nikka(
    id: NikkaId,
    createdAt: Nichiji,
)

object Nikka {
  def create[F[_]: Sync]: F[Nikka] = ???
}

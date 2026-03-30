package base

import fuda.*
import cats.effect.Sync

opaque type NikkaId <: Fuda.Id = Fuda.Id

sealed case class Nikka(
    id: NikkaId,
    createdAt: NichijiOld,
)

object Nikka {
  def create[F[_]: Sync]: F[Nikka] = ???
}

package base

import fuda.*
import cats.effect.Sync

opaque type NikkaId = UuidFuda

sealed case class Nikka(
    id: NikkaId,
    createdAt: Nichiji,
)

object Nikka {
  def create[F[_]: Sync]: F[Nikka] = ???
}

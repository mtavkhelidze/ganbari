package ge.zgharbi.ganbari.core

import cats.*
import cats.effect.Sync
import cats.syntax.all.*
import ge.zgharbi.ganbari.core.id.IdType

private object NikkaId {
  type Type = IdType[Tag]
  sealed trait Tag
}
type NikkaId = NikkaId.Type

sealed case class Nikka(
    id: NikkaId,
    createdAt: Nichiji,
)

object Nikka {
  def create[F[_]: Sync]: F[Nikka] = ???
}

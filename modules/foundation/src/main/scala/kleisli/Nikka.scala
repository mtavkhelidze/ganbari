package kleisli

import cats.*
import cats.effect.Sync
import cats.syntax.all.*
import kleisli.{IdType, Nikka}

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

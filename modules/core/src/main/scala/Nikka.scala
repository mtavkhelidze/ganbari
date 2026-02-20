package ge.zgharbi.ganbari.core
import id.{UuidFactory, UuidSyncFactory, UuidType}

import cats.*
import cats.effect.Sync
import cats.syntax.all.*

private object NikkaId {
  type Type = UuidType[Tag]
  sealed trait Tag
}
type NikkaId = NikkaId.Type

sealed case class Nikka(
    id: NikkaId,
    createdAt: Nichiji,
)

sealed trait NikkaFactory[F[_]] {
  def create(): F[Nikka]
}

class NikkaSyncFactory[F[_]: Sync](uf: UuidFactory[F]) extends NikkaFactory[F] {
  def create(): F[Nikka] =
    for {
      id <- uf.make[NikkaId]
      createdAt <- Nichiji[F]("2026-02-20T20:50", "+04:00")
    } yield Nikka(id, createdAt)
}

object NikkaFactory {
  def live[F[_]: Sync] = new NikkaSyncFactory[F](UuidSyncFactory[F])
}

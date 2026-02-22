package ge.zgharbi.ganbari.core

import id.{IdGen, IdType, UuidSyncId}

import cats.*
import cats.effect.*
import cats.syntax.all.*

private object DomainId {
  type Type = IdType[Tag]
  sealed trait Tag
}
type DomainId = DomainId.Type

val di: IO[DomainId] = new UuidSyncId[IO].make[DomainId]
case class Domain(
    id: DomainId,
    name: String,
)

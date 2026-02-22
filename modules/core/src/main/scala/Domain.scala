package ge.zgharbi.ganbari.core

import id.{IdFactory, IdGen, IdType}

import cats.*
import cats.effect.*
import cats.syntax.all.*

private object DomainId {
  type Type = IdType[Tag]
  sealed trait Tag
}
opaque type DomainId = DomainId.Type
val di: IO[DomainId] = IdFactory.uuid[IO].make[DomainId]

case class Domain(
    id: DomainId,
    name: String,
)

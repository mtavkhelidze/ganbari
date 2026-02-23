package ge.zgharbi.ganbari.core

import id.{IdFactory, IdType, IdTypeGen}

import cats.*
import cats.effect.*

private object DomainId { sealed trait Tag }
opaque type DomainId = IdType[DomainId.Tag]

val di = IdFactory.uuid[IO].make[DomainId]

case class Domain(
    id: DomainId,
    name: String,
)

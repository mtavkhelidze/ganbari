package schema

import id.{IdFactory, IdType}

import cats.*
import cats.effect.*

private object DomainId { sealed trait Tag }
opaque type DomainId = IdType[DomainId.Tag]

case class Domain(
    id: DomainId,
    name: String,
)

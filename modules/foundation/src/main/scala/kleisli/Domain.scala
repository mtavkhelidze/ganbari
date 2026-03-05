package kleisli

import cats.*
import cats.effect.*
import kleisli.id.IdFactory

private object DomainId { sealed trait Tag }
opaque type DomainId = IdType[DomainId.Tag]

case class Domain(
    id: DomainId,
    name: String,
)

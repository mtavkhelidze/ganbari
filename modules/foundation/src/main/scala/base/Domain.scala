package base

private object DomainId { sealed trait Tag }
opaque type DomainId = IdType[DomainId.Tag]

case class Domain(
    id: DomainId,
    name: String,
)

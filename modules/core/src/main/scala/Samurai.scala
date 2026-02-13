package ge.zgharbi.ganbari.core

import id.IdType

private trait Tag
private opaque type Id = IdType[Tag]

// todo: move into separate type if validation will be needed
private opaque type Password <: String = String

case class Samurai(
    id: Id,
    email: Email,
    password: String,
)

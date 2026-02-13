package ge.zgharbi.ganbari.core

import id.IdType

private trait Tag
private opaque type Id = IdType[Tag]

case class Samurai(
    id: Id
    
)

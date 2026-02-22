package ge.zgharbi.ganbari.core

import uuid_id.{UuidSyncFactory, UuidType}

import cats.effect.Sync
import cats.syntax.all.*

private trait Tag
private opaque type Id = UuidType[Tag]

// todo: move into separate type if validation will be needed
private opaque type Password <: String = String

case class Samurai(
    id: Id,
    email: Email,
    password: String,
)

//object Samurai {
//  def apply[F[_]: Sync](email: String, password: String): F[Samurai] =
//    IdManager[F]
//      .make[Id]
//      .flatMap(id => Email[F](email).map(Samurai(id, _, password)))
//}

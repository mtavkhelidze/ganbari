package kleisli

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import fuda.*

opaque type DomainId <: Fuda.Id = Fuda.Id

case class Domain(
    id: DomainId,
    name: String,
)

object Domain {
  def apply[F[_]]: Kleisli[F, String, Domain] = ???
  private def apply(id: DomainId, name: String): Domain = new Domain(id, name)
}

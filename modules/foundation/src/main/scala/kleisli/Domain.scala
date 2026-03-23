package kleisli

import cats.*
import cats.data.*
import cats.syntax.all.*
import cats.effect.*
import fuda.*
import fuda.given

opaque type DomainId = UuidFuda

val id = FudaFactory[IO].make[DomainId]

case class Domain(
    id: DomainId,
    name: String,
)

object Domain {
  def apply[F[_]: Sync](n: String): Kleisli[F, String, Domain] =
    ???

  private def apply(id: DomainId, name: String): Domain = new Domain(id, name)
}

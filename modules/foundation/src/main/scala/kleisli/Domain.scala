package kleisli

import cats.data.*
import cats.effect.*
import fuda.{*, given}

opaque type DomainId = UuidFuda
object DomainId extends Fuda[DomainId]

case class Domain(
    id: DomainId,
    name: String,
)

type Misha

object Domain {

  def apply[F[_]: Sync]: Kleisli[F, String, Domain] = {
    val factory = FudaFactory[F]

    for {
      id <- factory.make[DomainId].local((_: String) => ())
      name <- Kleisli.ask[F, String]
    } yield new Domain(id, name)
  }
  private def apply(id: DomainId, name: String): Domain = new Domain(id, name)
}

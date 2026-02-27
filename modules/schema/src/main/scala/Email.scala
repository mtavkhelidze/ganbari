package schema

import cats.effect.Sync
import cats.{Eq, Show}

opaque type Email <: String = String

object Email {
  def apply[F[_]: Sync](s: String): F[Email] = {
    if s.contains("@")
    then Sync[F].pure(s.asInstanceOf[Email])
    else Sync[F].raiseError(IllegalArgumentException("Invalid email string."))
  }

  extension (e: Email) def value: String = e

  implicit val eqInstance: Eq[Email] = Eq.instance[Email] { (e1, e2) =>
    e1.value == e2.value
  }

  implicit val showInstance: Show[Email] = Show.show[Email](_.value)
}

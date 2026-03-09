package kleisli

import cats.*
import cats.data.*
import cats.syntax.all.*

trait ValidateK[F[_]] {
  def email: Kleisli[F, String, String]
}

object ValidateK {
  def apply[F[_]](using ev: ValidateK[F]): ValidateK[F] = ev

  private[kleisli] def hasAt[F[_]](using
      ap: ApplicativeError[F, Throwable],
  ): Kleisli[F, String, String] =
    Kleisli { s =>
      if s.contains("@")
      then ap.pure(s)
      else ap.raiseError(RuntimeException("Missing @ symbol"))
    }
}

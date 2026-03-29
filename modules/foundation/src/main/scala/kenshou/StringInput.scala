package foundation
package kenshou

import cats.*
import cats.data.*

object StringInput {
  def notEmpty[F[_]: MonadThrow]: Kleisli[F, String, String] =
    check(_.nonEmpty, "String is empty")
}

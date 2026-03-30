package foundation
package kenshou

import cats.*
import cats.data.*

object StringInput {
  def isNotEmpty[F[_]: MonadThrow]: Kleisli[F, String, String] =
    check(_.nonEmpty, "String is empty")
}

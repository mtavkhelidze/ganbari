package foundation
package kenshou

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*

object StringInput {
  def notEmpty[F[_]: MonadThrow]: Kleisli[F, String, String] =
    check(_.nonEmpty, "String is empty")
}

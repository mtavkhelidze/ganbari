package foundation
package kenshou

import cats.*
import cats.data.*
import cats.syntax.all.*

private[kenshou] def check[F[_]: MonadThrow, A](
    predicate: A => Boolean,
    message: String,
): Kleisli[F, A, A] = {
  Kleisli
    .ask[F, A]
    .flatMap(a =>
      if predicate(a) then a.pure
      else IllegalArgumentException(message).raiseError,
    )
}

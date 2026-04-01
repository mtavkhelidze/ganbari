package foundation
package kenshou

import kleisli.Nichiji

import cats.*
import cats.data.*

object NichijiInput {
  def isViableMonthlyDay[F[_]: MonadThrow]: Kleisli[F, Nichiji, Nichiji] =
    check(_.date.getDayOfMonth <= 28, "Day is not a valid monthly day")
}

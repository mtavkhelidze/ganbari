package foundation
package kleisli

import kenshou.StringInput

import cats.*
import cats.data.*

opaque type OuString <: String = String

object OuString {
  def apply[F[_]: MonadThrow]: Kleisli[F, String, OuString] =
    StringInput.isNotEmpty[F]

  private def apply(s: String): OuString = s
}

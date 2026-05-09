package front
package grpc

import cats.*
import cats.data.*
import cats.syntax.all.*
import io.grpc.*

final case class Context(tz: String)

object Context {
  private def read[F[_]: MonadThrow](
      k: String,
  ): Kleisli[F, Metadata, String] = {
    Kleisli(md => {
      md.get(Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER)) match
        case null =>
          MonadThrow[F].raiseError(
            IllegalArgumentException(s"$k header must be set"),
          )
        case value => MonadThrow[F].pure(value)

    })
  }

  def apply[F[_]: MonadThrow]: Kleisli[F, Metadata, Context] = {
    read("tz").map(Context(_))
  }
}

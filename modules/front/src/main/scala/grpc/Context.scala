package front
package grpc

import cats.*
import cats.data.*
import cats.syntax.all.*
import io.grpc.*

final case class Context(tz: String)

object Context {
  private def read[F[_]: MonadThrow](k: String)(md: Metadata): F[String] =
    md.get(Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER)) match
      case null =>
        MonadThrow[F].raiseError(
          IllegalArgumentException("TZ header must be set"),
        )
      case value => MonadThrow[F].unit.map(_ => value)

  def apply[F[_]: MonadThrow]: Kleisli[F, Metadata, Context] = {
    Kleisli
      .ask[F, Metadata]
      .flatMap(read("tz"))
      .map(tz => new Context(tz))
  }
}

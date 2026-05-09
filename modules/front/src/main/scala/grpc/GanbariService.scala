package front
package grpc

import grpc.types.ganbari.*

import cats.*
import cats.data.*
import cats.syntax.all.*
import foundation.kleisli.Nichiji

object GanbariService {
  def apply[F[_]: MonadThrow] = {
    new GanbariServiceFs2Grpc[F, Kleisli[F, Unit, Context]] {
      override def ping(
          request: PingRequest,
          ctx: ReaderT[F, Unit, Context],
      ): F[PingResponse] = {
        Kleisli
          .pure(PingResponse.apply.curried)
          .ap(ctx.map(_.tz) >>> Nichiji.now[F].map(_.toString))
          .ap(Nichiji.nowUtc[F].map(_.toString))
          .run(())
      }
    }
  }
}

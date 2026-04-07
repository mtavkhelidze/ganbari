package front
package grpc

import grpc.types.ganbari.{ GanbariServiceFs2Grpc, PingRequest, PingResponse }

import cats.*
import foundation.kleisli.*

object GanbariService {
  def apply[F[_]: MonadThrow]: GanbariServiceFs2Grpc[F, Unit] =
    new GanbariServiceFs2Grpc[F, Unit] {
      override def ping(request: PingRequest, ctx: Unit): F[PingResponse] =
        ???
        //Nichiji[F].now(PingResponse.newBuilder().build())
    }
}

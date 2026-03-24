package fuda

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*

trait FudaFactory[F[_]: MonadThrow] {
  def restore[A](using ctx:FudaContext[F, A]): Kleisli[F, String, A] =
    ctx.svc.read >>> Kleisli(r => MonadThrow[F].pure(ctx.from(r)))
  def make[A](using ctx: FudaContext[F, A]): Kleisli[F, Unit, A] =
    ctx.svc.write >>> Kleisli(r => MonadThrow[F].pure(ctx.from(r)))
}

object FudaFactory {
  def apply[F[_]: MonadThrow]: FudaFactory[F] = new FudaFactory[F] {}
}

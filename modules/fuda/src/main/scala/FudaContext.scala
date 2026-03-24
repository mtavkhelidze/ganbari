package fuda

import cats.*

import java.util.UUID
trait FudaContext[F[_], A] {
  private[fuda] type Und
  private[fuda] val svc: FudaService[F, Und]

  private[fuda] def from(u: Und): A
}

object FudaContext {
  given derived[F[_]: MonadThrow, UserType, FudaType](using
      ev: UserType =:= FudaType,
      fc: FudaContext[F, FudaType],
  ): FudaContext[F, UserType] = fc.asInstanceOf
}

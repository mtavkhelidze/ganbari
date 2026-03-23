package fuda

import cats.*
import cats.effect.*

import java.util.UUID

opaque type Fuda[Und] = Und
opaque type UuidFuda = Fuda[UUID]
opaque type LongFuda = Fuda[Long]
export FudaFactory.*

given [F[_]: Sync](using s: FudaService[F, UUID]): FudaContext[
  F,
  UuidFuda,
] with {
  type Und = UUID
  val svc = s

  def from(u: UUID): UuidFuda = u
}

given [F[_]: Sync](using s: FudaService[F, Long]): FudaContext[
  F,
  LongFuda,
] with {
  type Und = Long
  val svc = s

  def from(u: Long): LongFuda = u
}

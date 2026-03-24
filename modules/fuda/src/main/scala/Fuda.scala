package fuda

import cats.*
import cats.effect.*

import java.util.UUID

opaque type Fuda[Und] = Und
opaque type UuidFuda = Fuda[UUID]
opaque type LongFuda = Fuda[Long]
export FudaFactory.*

extension (u: UuidFuda) def value: UUID = u

extension (l: LongFuda) def value: Long = l

given [A: Eq]: Eq[(Fuda[A], A)] with
  def eqv(fa: (Fuda[A], A), fb: (Fuda[A], A)): Boolean =
    val (l, _) = fa
    val (_, r) = fb
    l == r

given [F[_]: MonadThrow](using s: FudaService[F, UUID]): FudaContext[
  F,
  UuidFuda,
] with {
  type Und = UUID
  val svc = s

  def from(u: UUID): UuidFuda = u
}

given [F[_]: MonadThrow](using s: FudaService[F, Long]): FudaContext[
  F,
  LongFuda,
] with {
  type Und = Long
  val svc = s

  def from(u: Long): LongFuda = u
}

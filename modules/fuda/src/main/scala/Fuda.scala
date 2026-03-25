package fuda

import FudaService.given

import cats.*
import cats.effect.*

import java.util.UUID

private opaque type FudaBase[Und] = Und
opaque type UuidFuda = FudaBase[UUID]
//opaque type LongFuda = FudaBase[Long]

trait Fuda[A] {
  inline given [F[_]: Sync]: FudaContext[F, A] = ${
    FudaContext.derivedMacro[F, A]
  }
  inline given eq: Eq[A] = ${ FudaMacros.eq[A] }

  extension (a: A) {
    inline def value = ${ FudaMacros.value[A]('a) }
  }
}

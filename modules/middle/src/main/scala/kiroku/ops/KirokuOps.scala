package middle
package kiroku.ops

import back.storage.KirokuRow
import cats.*
import cats.effect.*
import cats.syntax.all.*
import foundation.kleisli.{Kiroku, Nichiji}

import java.util.UUID

export KirokuOps.*

object KirokuOps {
  private def typeName(k: Kiroku): String = k match
    case _: Kiroku.Koutou => "Koutou"

  private def variantName(k: Kiroku): String = k match
    case _: Kiroku.Koutou => "Koutou"

  private def idValue(k: Kiroku): UUID = k.id

  extension [F[_]: MonadThrow](k: Kiroku) {
    def toRow: F[KirokuRow] = ???
//      Nichiji
//      .now[F]
//      .map(now =>
//        KirokuRow(
//          entityData = Map.empty,
//          entityId = idValue(k),
//          entityType = typeName(k),
//          id = k.id,
//          ts = now.instant,
//          variant = variantName(k),
//        ),
//      )
  }
}

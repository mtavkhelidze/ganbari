package back
package storage

import cats.MonadThrow

import java.time.Instant
import java.util.UUID

trait KirokuWriter[F[_]: MonadThrow] {
  def write(k: KirokuRow): F[Unit]
}

case class KirokuRow(
    entityData: Map[String, String],
    entityId: UUID,
    entityType: String,
    id: UUID,
    ts: Instant,
    variant: String,
)

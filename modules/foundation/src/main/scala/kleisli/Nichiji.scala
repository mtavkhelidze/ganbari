package foundation
package kleisli

import cats.*
import cats.data.*
import cats.syntax.all.*

import java.time.*

case class Nichiji private (ts: LocalDateTime, tz: ZoneOffset)

object Nichiji {
  private[kleisli] def parseTs[F[_]: MonadThrow]
      : Kleisli[F, String, LocalDateTime] =
    Kleisli(s =>
      MonadThrow[F]
        .catchNonFatal(LocalDateTime.parse(s))
        .adaptError(e => IllegalArgumentException(e)),
    )
  private[kleisli] def parseTz[F[_]: MonadThrow]
      : Kleisli[F, String, ZoneOffset] =
    Kleisli(s =>
      MonadThrow[F]
        .catchNonFatal(ZoneOffset.of(s))
        .adaptError(e => IllegalArgumentException(e)),
    )

  type Input = (ts: String, tz: String)
  def apply[F[_]: MonadThrow]: Kleisli[F, (ts: String, tz: String), Nichiji] =
    (
      parseTs[F].lmap[Input](_.ts)
        product parseTz[F].lmap[Input](_.tz)
    ) map (Nichiji(_, _))

  extension (nt: Nichiji) {
    def date: LocalDate = nt.ts.toLocalDate
    def dateUtc: LocalDate = instant.atOffset(ZoneOffset.UTC).toLocalDate
    def instant: Instant = nt.iso.toInstant
    def isAfter(other: Nichiji): Boolean = nt.instant.isAfter(other.instant)
    def isBefore(other: Nichiji): Boolean = nt.instant.isBefore(other.instant)
    def iso: OffsetDateTime = nt.ts.atOffset(nt.tz)
    def unix: Long = instant.getEpochSecond
  }
}

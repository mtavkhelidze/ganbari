package base

import cats.*
import cats.effect.*
import cats.syntax.all.*
import base.Nichiji

import java.time.*

// @note: Nichiji (日時) is Date and Time in Japanese
final case class Nichiji(
    ts: LocalDateTime,
    tz: ZoneOffset,
)

object Nichiji {
  def apply[F[_]: Sync](ts: String, tz: String): F[Nichiji] =
    for {
      dt <- Sync[F].catchNonFatal(LocalDateTime.parse(ts))
      zo <- Sync[F].catchNonFatal(ZoneOffset.of(tz))
    } yield Nichiji(dt, zo)

  extension (nt: Nichiji) {
    def date: LocalDate = nt.ts.toLocalDate
    def instant: Instant = nt.ts.atOffset(nt.tz).toInstant
    def isAfter(other: Nichiji): Boolean = nt.instant.isAfter(other.instant)
    def isBefore(other: Nichiji): Boolean = nt.instant.isBefore(other.instant)
    def iso: OffsetDateTime = nt.ts.atOffset(nt.tz)
    def unix: Long = instant.getEpochSecond
    def utcDate: LocalDate = instant.atOffset(ZoneOffset.UTC).toLocalDate
  }

  given Eq[Nichiji] = (a, b) => a.instant == b.instant

  given Show[Nichiji] = (nt) => nt.iso.toString

  given Ordering[Nichiji] = (x, y) => x.instant.compareTo(y.instant)
}

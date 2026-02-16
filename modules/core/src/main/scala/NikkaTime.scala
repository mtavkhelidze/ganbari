package ge.zgharbi.ganbari.core

import cats.*
import cats.effect.*
import cats.syntax.all.*

import java.time.*

final case class NikkaTime(
    ts: LocalDateTime,
    tz: ZoneOffset,
)

object NikkaTime {
  def apply[F[_]: Sync](ts: String, tz: String): F[NikkaTime] =
    for {
      dt <- Sync[F].catchNonFatal(LocalDateTime.parse(ts))
      zo <- Sync[F].catchNonFatal(ZoneOffset.of(tz))
    } yield NikkaTime(dt, zo)

  extension (nt: NikkaTime) {
    def date: LocalDate = nt.ts.toLocalDate
    def instant: Instant = nt.ts.atOffset(nt.tz).toInstant()
    def isAfter(other: NikkaTime): Boolean = nt.instant.isAfter(other.instant)
    def isBefore(other: NikkaTime): Boolean = nt.instant.isBefore(other.instant)
    def iso: OffsetDateTime = nt.ts.atOffset(nt.tz)
    def unix: Long = instant.getEpochSecond
    def utcDate: LocalDate = date.atStartOfDay(ZoneOffset.UTC).toLocalDate
  }

  given Eq[NikkaTime] with {
    override def eqv(a: NikkaTime, b: NikkaTime): Boolean =
      a.instant == b.instant
  }

  given Show[NikkaTime] with {
    override def show(nt: NikkaTime): String = nt.iso.toString
  }

  given Ordering[NikkaTime] with {
    override def compare(x: NikkaTime, y: NikkaTime): Int =
      x.instant.compareTo(y.instant)
  }
}

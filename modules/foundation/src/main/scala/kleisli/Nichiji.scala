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

  extension (nj: Nichiji) {
    def date: LocalDate = nj.iso.toLocalDate
    def dateUtc: LocalDate = instant.atOffset(ZoneOffset.UTC).toLocalDate
    def dayOfWeek: DayOfWeek = nj.iso.toLocalDate.getDayOfWeek
    def instant: Instant = nj.iso.toInstant
    def isAfter(other: Nichiji): Boolean = nj.instant.isAfter(other.instant)
    def isBefore(other: Nichiji): Boolean = nj.instant.isBefore(other.instant)
    def iso: OffsetDateTime = nj.ts.atOffset(nj.tz)
    def unix: Long = instant.getEpochSecond
  }
}

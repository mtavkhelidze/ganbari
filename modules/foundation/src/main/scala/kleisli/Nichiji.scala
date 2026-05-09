package foundation
package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import cats.syntax.all.*

import java.time.*

/** Obeserver's time.
  */
case class Nichiji private (tz: ZoneOffset, ts: LocalDateTime)

object Nichiji {
  private[kleisli] def parseTs[F[_]: MonadThrow]
      : Kleisli[F, String, LocalDateTime] =
    Kleisli(s =>
      MonadThrow[F]
        .catchNonFatal(LocalDateTime.parse(s))
        .adaptError(e => IllegalArgumentException(e)),
    )

  private def parseTz[F[_]: MonadThrow]: Kleisli[F, String, ZoneOffset] =
    Kleisli(s =>
      MonadThrow[F]
        .catchNonFatal(ZoneOffset.of(s))
        .adaptError(e => IllegalArgumentException(e)),
    )

  // @todo: those Strings need to be opaque typed
  type Input = (tz: String, ts: String)
  def apply[F[_]: MonadThrow]: Kleisli[F, Input, Nichiji] = {
    Kleisli
      .ask[F, Input]
      .flatMap(i =>
        Kleisli
          .pure(Nichiji.apply.curried)
          .ap(parseTz[F].lmap(_ => i.tz))
          .ap(parseTs[F].lmap(_ => i.ts)),
      )
  }

  def nowUtc[F[_]: MonadThrow]: ReaderT[F, Unit, Nichiji] =
    Kleisli
      .ask[F, Unit]
      .map(_ => Nichiji(ZoneOffset.UTC, LocalDateTime.now(ZoneOffset.UTC)))

  def now[F[_]: MonadThrow]: Kleisli[F, String, Nichiji] =
    Kleisli
      .ask[F, String]
      .andThen(parseTz[F])
      .map(tz => Nichiji(tz, LocalDateTime.now(tz)))

  extension (nj: Nichiji) {
    def date: LocalDate = nj.iso.toLocalDate
    def dateUtc: LocalDate = instant.atOffset(ZoneOffset.UTC).toLocalDate
    def dayOfWeek: DayOfWeek = nj.iso.toLocalDate.getDayOfWeek
    def instant: Instant = nj.iso.toInstant
    def isAfter(other: Nichiji): Boolean = nj.instant.isAfter(other.instant)
    def isBefore(other: Nichiji): Boolean = nj.instant.isBefore(other.instant)
    def iso: OffsetDateTime = nj.ts.atOffset(nj.tz)
    def unix: Long = instant.getEpochSecond
    def now: Nichiji = Nichiji(nj.tz, LocalDateTime.now(nj.tz))
  }
}

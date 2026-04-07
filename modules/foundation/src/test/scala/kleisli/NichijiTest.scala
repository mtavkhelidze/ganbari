package foundation
package kleisli

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import cats.syntax.all.*
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.freespec.AsyncFreeSpec

import java.time.ZoneOffset

val validInput: Nichiji.Input = (tz = "+04:00", ts = "2026-02-16T17:30")
val epochZero: Nichiji.Input = (tz = "+00:00", ts = "1970-01-01T00:00")
val tzGST = "+04:00"
val offsetGST = ZoneOffset.of(tzGST).getTotalSeconds.toLong

class NichijiTest
    extends AsyncFreeSpec
    with AsyncIOSpec
    with TypeCheckedTripleEquals {
  "Extensions" - {
    "#date returns correct local date" in {
      Nichiji[IO]
        .map(nj => assert(nj.date === java.time.LocalDate.of(1970, 1, 1)))
        .run(epochZero)
    }
    "#date returns local date in user's timezone" in {
      Nichiji[IO]
        .map(nj =>
          assert(
            nj.date === java.time.LocalDate.of(2026, 2, 16)
              && nj.dateUtc === java.time.LocalDate.of(2026, 2, 15),
          ),
        )
        .run((tz = "+04:00", ts = "2026-02-16T01:00"))
    }
    "#dateUtc returns correct local date" in {
      Nichiji[IO]
        .map(nj => assert(nj.dateUtc === java.time.LocalDate.of(1969, 12, 31)))
        .run((tz = tzGST, ts = epochZero.ts))
    }
    "#isAfter returns true when the first is after the second" in {
      (Nichiji[IO] product Nichiji[IO]
        .lmap[Nichiji.Input] { case (tz, _) => (tz = tz, ts = epochZero.ts) })
        .map { case (later, earlier) => assert(later.isAfter(earlier)) }
        .run(validInput)
    }
    "#isAfter returns false when first is before second" in {
      (Nichiji[IO] product Nichiji[IO]
        .lmap[Nichiji.Input] { case (tz, _) => (tz, epochZero.ts) })
        .map { case (later, earlier) => assert(!earlier.isAfter(later)) }
        .run(validInput)
    }
    "#isBefore returns true when first is before second" in {
      (Nichiji[IO] product Nichiji[IO]
        .lmap[Nichiji.Input] { case (tz, _) => (tz, epochZero.ts) })
        .map { case (later, earlier) => assert(earlier.isBefore(later)) }
        .run(validInput)
    }
    "#isBefore returns false when first is after second" in {
      (Nichiji[IO] product Nichiji[IO]
        .lmap[Nichiji.Input] { case (tz, _) => (tz, epochZero.ts) })
        .map { case (later, earlier) => assert(!later.isBefore(earlier)) }
        .run(validInput)
    }
    "#isAfter is false for equal instants" in {
      (Nichiji[IO] product Nichiji[IO])
        .map { case (a, b) => assert(!a.isAfter(b) && !b.isAfter(a)) }
        .run(epochZero)
    }
    "#isAfter and #isBefore are reverse of each other" in {
      (Nichiji[IO] product Nichiji[IO]
        .lmap[Nichiji.Input] { case (tz, _) => (tz, epochZero.ts) })
        .map { case (later, earlier) =>
          assert(later.isAfter(earlier) === earlier.isBefore(later))
          assert(earlier.isAfter(later) === later.isBefore(earlier))
        }
        .run(validInput)
    }
    "#isAfter is transitive" in {
      val earliest = epochZero
      val middle = (tz = "+00:00", ts = "1970-01-01T12:00")
      (Nichiji[IO] product Nichiji[IO]
        .lmap[Nichiji.Input](_ => middle) product Nichiji[IO]
        .lmap[Nichiji.Input](_ => earliest))
        .map { case ((latest, mid), earliest) =>
          assert(latest.isAfter(mid))
          assert(mid.isAfter(earliest))
          assert(latest.isAfter(earliest))
        }
        .run(validInput)
    }
  }
  "Construction" - {
    "happy path with valid input" in {
      Nichiji[IO]
        .map(nj => (nj.tz.toString, nj.ts.toString))
        .map(x => assert(x === validInput))
        .run(validInput)
    }
    "fails with invalid input" in {
      Nichiji[IO]
        .assertThrows[IllegalArgumentException]
        .run(("not a date", validInput.tz))
      Nichiji[IO]
        .assertThrows[IllegalArgumentException]
        .run((validInput.ts, "not a timezone"))
    }
    "fails with empty input" in {
      Nichiji[IO]
        .assertThrows[IllegalArgumentException]
        .run(("", ""))
    }
    "handles timezone correctly" in {
      (Nichiji[IO]
        product
          Nichiji[IO].lmap[Nichiji.Input] { case (_, ts) => (tzGST, ts) })
        .map { case (i1, i2) =>
          assert(
            i1.unix - i2.unix === offsetGST,
          )
        }
        .run(epochZero)
    }
  }
}

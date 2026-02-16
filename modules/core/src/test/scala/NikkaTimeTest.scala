package ge.zgharbi.ganbari.core

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate}

val validEpochBeginDateTime = "1970-01-01T00:00:00"
val validDateTime = "2026-02-16T17:30"
val tzUTC = "+00:00"
val tzGST = "+04:00"
val invalidDateTime = "1970/01/02 17:30"
val invalidTimeZone = "+25:15"

class NikkaTimeTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "#apply" - {
    "should create NikkaTime from valid strings" in {
      NikkaTime[IO](validEpochBeginDateTime, tzUTC).attempt.map {
        case Left(_) =>
          fail("Expected creation to succeed with valid datetime and timezone")
        case Right(nt) => nt.unix shouldEqual 0
      }
    }

    "should fail to create NikkaTime from invalid date string" in {
      NikkaTime[IO](invalidDateTime, tzUTC).attempt.map {
        case Right(_) => fail("Expected failure for invalid datetime string")
        case Left(_)  => succeed
      }
    }

    "should fail to create NikkaTime from invalid timezone string" in {
      NikkaTime[IO](validEpochBeginDateTime, invalidTimeZone).attempt.map {
        case Right(_) => fail("Expected failure for invalid timezone string")
        case Left(_)  => succeed
      }
    }

    "should create NikkaTime with non-UTC offset correctly" in {
      NikkaTime[IO](validEpochBeginDateTime, tzGST).attempt.map {
        case Left(_) =>
          fail("Expected creation to succeed with valid non-UTC offset")
        case Right(nt) =>
          nt.unix shouldEqual -14400 // 1970-01-01T00:00:00+04:00 → UTC = -4h = -14400s
      }
    }
  }
  "#date" - {
    "should return correct local date for a given NikkaTime" in {
      NikkaTime[IO](validDateTime, tzGST).map { nt =>
        nt.date shouldEqual java.time.LocalDate.of(2026, 2, 16)
      }
    }

    "should return correct local date for UTC offset" in {
      NikkaTime[IO](validEpochBeginDateTime, tzUTC).map { nt =>
        nt.date shouldEqual java.time.LocalDate.of(1970, 1, 1)
      }
    }

    "should handle non-UTC offsets correctly" in {
      NikkaTime[IO](validEpochBeginDateTime, tzGST).map { nt =>
        nt.date shouldEqual java.time.LocalDate.of(1970, 1, 1)
      }
    }
  }
  "#instant" - {
    "should produce correct UTC Instant" in {
      NikkaTime[IO](validEpochBeginDateTime, tzUTC).map { nt =>
        nt.instant shouldEqual Instant.ofEpochSecond(0)
      }
    }
    "should produce correct UTC Instant for non-UTC offset" in {
      NikkaTime[IO](validEpochBeginDateTime, tzGST).map { nt =>
        nt.instant shouldEqual Instant.ofEpochSecond(-14400)
      }
    }
  }
  "#iso" - {
    "should return correct OffsetDateTime with correct offset" in {
      NikkaTime[IO](validDateTime, tzGST).map { nt =>
        val iso = nt.iso
        iso.getOffset.getId shouldEqual tzGST
        iso.toLocalDateTime.toString shouldEqual validDateTime
      }
    }
  }
  "#utcDate" - {
    "should return correct UTC date" in {
      NikkaTime[IO](validDateTime, tzGST).map { nt =>
        nt.utcDate shouldEqual LocalDate.of(2026, 2, 16)
      }
    }
    "should return correct UTC date for epoch" in {
      NikkaTime[IO](validEpochBeginDateTime, tzGST).map { nt =>
        nt.utcDate shouldEqual LocalDate.of(1970, 1, 1)
      }
    }
  }
  "#comparisons" - {
    "should correctly compare isAfter and isBefore" in {
      for {
        nt1 <- NikkaTime[IO](validEpochBeginDateTime, tzUTC)
        nt2 <- NikkaTime[IO]("1970-01-01T00:00:01", tzUTC)
      } yield {
        nt2.isAfter(nt1) shouldBe true
        nt1.isBefore(nt2) shouldBe true
        nt1.isAfter(nt2) shouldBe false
        nt2.isBefore(nt1) shouldBe false
      }
    }
  }
  "#typeclasses" - {
    import cats.{Eq, Show}

    "should have correct Eq instance" in {
      for {
        nt1 <- NikkaTime[IO](validEpochBeginDateTime, tzUTC)
        nt2 <- NikkaTime[IO](validEpochBeginDateTime, tzUTC)
        nt3 <- NikkaTime[IO]("1970-01-01T00:00:01", tzUTC)
      } yield {
        Eq[NikkaTime].eqv(nt1, nt2) shouldBe true
        Eq[NikkaTime].eqv(nt1, nt3) shouldBe false
      }
    }

    "should have correct Show instance" in {
      NikkaTime[IO](validEpochBeginDateTime, tzUTC).map { nt =>
        nt.show shouldEqual "1970-01-01T00:00Z"
      }
      NikkaTime[IO](validDateTime, tzGST).map { nt =>
        nt.show shouldEqual (validDateTime + tzGST)
      }
    }

    "should have correct Order instance" in {
      for {
        ntEpoch <- NikkaTime[IO](validEpochBeginDateTime, tzUTC)
        ntGeo <- NikkaTime[IO](validDateTime, tzGST)
      } yield {
        Ordering[NikkaTime].compare(ntEpoch, ntEpoch) shouldEqual 0
        Ordering[NikkaTime].compare(ntEpoch, ntGeo) should be < 0
        Ordering[NikkaTime].compare(ntGeo, ntEpoch) should be > 0
      }
    }
  }
}

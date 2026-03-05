package kleisli

import cats.Eq
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import cats.syntax.all.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import kleisli.Nichiji

import java.time.{Instant, LocalDate}

val validEpochBeginDateTime = "1970-01-01T00:00:00"
val validDateTime = "2026-02-16T17:30"
val tzUTC = "+00:00"
val tzGST = "+04:00"
val invalidDateTime = "1970/01/02 17:30"
val invalidTimeZone = "+25:15"

class NichijiTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "#apply" - {
    "should create Nichiji from valid strings" in {
      Nichiji[IO](validEpochBeginDateTime, tzUTC).attempt.map {
        case Left(_) =>
          fail("Expected creation to succeed with valid datetime and timezone")
        case Right(nt) => nt.unix shouldEqual 0
      }
    }

    "should fail to create Nichiji from invalid date string" in {
      Nichiji[IO](invalidDateTime, tzUTC).attempt.map {
        case Right(_) => fail("Expected failure for invalid datetime string")
        case Left(_)  => succeed
      }
    }

    "should fail to create Nichiji from invalid timezone string" in {
      Nichiji[IO](validEpochBeginDateTime, invalidTimeZone).attempt.map {
        case Right(_) => fail("Expected failure for invalid timezone string")
        case Left(_)  => succeed
      }
    }

    "should create Nichiji with non-UTC offset correctly" in {
      Nichiji[IO](validEpochBeginDateTime, tzGST).attempt.map {
        case Left(_) =>
          fail("Expected creation to succeed with valid non-UTC offset")
        case Right(nt) =>
          nt.unix shouldEqual -14400 // 1970-01-01T00:00:00+04:00 → UTC = -4h = -14400s
      }
    }
  }
  "#date" - {
    "should return correct local date for a given Nichiji" in {
      Nichiji[IO](validDateTime, tzGST).map { nj =>
        nj.date shouldEqual java.time.LocalDate.of(2026, 2, 16)
      }
    }

    "should return correct local date for UTC offset" in {
      Nichiji[IO](validEpochBeginDateTime, tzUTC).map { nj =>
        nj.date shouldEqual java.time.LocalDate.of(1970, 1, 1)
      }
    }

    "should handle non-UTC offsets correctly" in {
      Nichiji[IO](validEpochBeginDateTime, tzGST).map { nj =>
        nj.date shouldEqual java.time.LocalDate.of(1970, 1, 1)
      }
    }
  }
  "#instant" - {
    "should produce correct UTC Instant" in {
      Nichiji[IO](validEpochBeginDateTime, tzUTC).map { nj =>
        nj.instant shouldEqual Instant.ofEpochSecond(0)
      }
    }
    "should produce correct UTC Instant for non-UTC offset" in {
      Nichiji[IO](validEpochBeginDateTime, tzGST).map { nj =>
        nj.instant shouldEqual Instant.ofEpochSecond(-14400)
      }
    }
    "should handle negative unix timestamps correctly" in {
      Nichiji[IO]("1969-12-31T23:59:59", tzUTC).map { nj =>
        nj.unix shouldEqual -1
      }
    }
  }
  "#iso" - {
    "should return correct OffsetDateTime with correct offset" in {
      Nichiji[IO](validDateTime, tzGST).map { nj =>
        val iso = nj.iso
        iso.getOffset.getId shouldEqual tzGST
        iso.toLocalDateTime.toString shouldEqual validDateTime
      }
    }
    "iso instant should match internal instant" in {
      Nichiji[IO](validDateTime, tzGST).map { nj =>
        nj.iso.toInstant shouldEqual nj.instant
      }
    }
  }
  "#utcDate" - {
    "should correctly shift the date when calculating utcDate across midnight" in {
      // 1 AM in Tbilisi (+04:00) on the 21st is 9 PM UTC on the 20th
      Nichiji[IO]("2026-02-21T01:00:00", tzGST).map { nj =>
        nj.date shouldEqual LocalDate.of(2026, 2, 21) // Local
        nj.utcDate shouldEqual LocalDate.of(2026, 2, 20) // UTC
      }
    }
    "should return correct UTC date" in {
      Nichiji[IO](validDateTime, tzGST).map { nj =>
        nj.utcDate shouldEqual LocalDate.of(2026, 2, 16)
      }
    }
    "should return correct UTC date for epoch" in {
      Nichiji[IO](validEpochBeginDateTime, tzGST).map { nj =>
        nj.utcDate shouldEqual LocalDate.of(1969, 12, 31)
      }
    }
    "should shift utcDate forward when offset is negative" in {
      // 23:00 at -02:00 is 01:00 UTC next day
      Nichiji[IO]("2026-02-20T23:00:00", "-02:00").map { nj =>
        nj.utcDate shouldEqual LocalDate.of(2026, 2, 21)
      }
    }
  }
  "#comparisons" - {
    "should consider different local times as equal if they represent the same instant" in {
      for {
        njTbilisi <- Nichiji[IO]("2026-02-20T20:00:00", tzGST)
        njUTC <- Nichiji[IO]("2026-02-20T16:00:00", tzUTC)
      } yield {
        (njTbilisi =!= njUTC) shouldBe false
      }
    }
    "should correctly compare isAfter and isBefore" in {
      for {
        nj1 <- Nichiji[IO](validEpochBeginDateTime, tzUTC)
        nj2 <- Nichiji[IO]("1970-01-01T00:00:01", tzUTC)
      } yield {
        nj2.isAfter(nj1) shouldBe true
        nj1.isBefore(nj2) shouldBe true
        nj1.isAfter(nj2) shouldBe false
        nj2.isBefore(nj1) shouldBe false
      }
    }
    "Order should be consistent with Eq" in {
      for {
        nj1 <- Nichiji[IO]("2026-02-20T20:00:00", tzGST)
        nj2 <- Nichiji[IO]("2026-02-20T16:00:00", tzUTC)
      } yield {
        Eq[Nichiji].eqv(nj1, nj2) shouldBe true
        Ordering[Nichiji].compare(nj1, nj2) shouldBe 0
      }
    }
  }
  "#typeclasses" - {
    import cats.Eq

    "should have correct Eq instance" in {
      for {
        nj1 <- Nichiji[IO](validEpochBeginDateTime, tzUTC)
        nj2 <- Nichiji[IO](validEpochBeginDateTime, tzUTC)
        nj3 <- Nichiji[IO]("1970-01-01T00:00:01", tzUTC)
      } yield {
        Eq[Nichiji].eqv(nj1, nj2) shouldBe true
        Eq[Nichiji].eqv(nj1, nj3) shouldBe false

        // reflexive
        Eq[Nichiji].eqv(nj1, nj1) shouldBe true

        // symmetric
        Eq[Nichiji].eqv(nj1, nj2) shouldBe Eq[Nichiji].eqv(nj2, nj1)
      }
    }

    "should have correct Show instance" in {
      Nichiji[IO](validEpochBeginDateTime, tzUTC).map { nj =>
        nj.show shouldEqual "1970-01-01T00:00Z"
      }
      Nichiji[IO](validDateTime, tzGST).map { nj =>
        nj.show shouldEqual (validDateTime + tzGST)
      }
    }

    "should have correct Order instance" in {
      for {
        njEpoch <- Nichiji[IO](validEpochBeginDateTime, tzUTC)
        njGeo <- Nichiji[IO](validDateTime, tzGST)
      } yield {
        Ordering[Nichiji].compare(njEpoch, njEpoch) shouldEqual 0
        Ordering[Nichiji].compare(njEpoch, njGeo) should be < 0
        Ordering[Nichiji].compare(njGeo, njEpoch) should be > 0
      }
    }
  }
}

package fuda

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.freespec.AsyncFreeSpec

import java.util.UUID

opaque type TestId <: Fuda.Type = Fuda.Type
opaque type TestIdOne <: Fuda.Type = Fuda.Type
opaque type TestIdTwo <: Fuda.Type = Fuda.Type

// AI generated. Errors are not mine.
val uuidRegex =
  "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"

val validUuid = "22124234-196c-4d0d-af5c-1a8da7486259"
val notUuid = "0xDEADBEEF"

class FudaTest
    extends AsyncFreeSpec
    with AsyncIOSpec
    with TypeCheckedTripleEquals {

  "Operations" - {
    "Fuda#make -> Fuda@read roundtrip" in {
      Fuda[TestId]
        .make[IO]
        .map(id => (id.toString, id))
        .flatMap { case (str, generated) =>
          Fuda[TestId]
            .read[IO]
            .lmap(_ => str)
            .rmap(parsed => (parsed, generated))
        }
        .map { case (parsed, generated) => assert(parsed === generated) }
        .run(())
    }
    "Fuda#make produces a valid UUID" in {
      Fuda[TestId]
        .make[IO]
        .map(_.toString.matches(uuidRegex))
        .map(assert(_))
        .run(())
    }
    "Fuda#read recovers the original UUID" in {
      Fuda[TestId]
        .read[IO]
        .map(_.toString)
        .map(x => assert(x === validUuid))
        .run(validUuid)
    }
    "Fuda#read fails with IllegalArgumentException for invalid UUID" in {
      Fuda[TestId]
        .read[IO]
        .assertThrows[IllegalArgumentException]
        .run(notUuid)
    }
  }

  "UuidProviders" - {
    "UuidMaker is replaceable by a given in scope" in {
      given UuidMaker[IO] = new UuidMaker[IO] {
        def make: IO[UUID] = IO.delay(UUID.fromString(validUuid))
      }
      Fuda[TestId]
        .make[IO]
        .map(_.toString)
        .map(x => assert(x === validUuid))
        .run(())
    }

    "UuidReader is replaceable by a given in scope" in {
      given UuidReader[IO] = new UuidReader[IO] {
        def read(s: String): IO[UUID] =
          IO.delay(UUID.fromString(notUuid))
            .recoverWith { case _: IllegalArgumentException =>
              IO.delay(UUID.fromString(validUuid))
            }
      }
      Fuda[TestId]
        .read[IO]
        .map(_.toString)
        .map(x => assert(x === validUuid))
        .run(validUuid)
    }
  }
  "Fuda.Type" - {
    "opaque aliases are unique" in {
      assertDoesNotCompile("""
          |(Fuda[TestIdOne].read[IO],
          |Fuda[TestIdTwo].read[IO])
          |  .mapN(_ === _)
          |  .map(assert(_))
          |""".stripMargin)
    }
  }
}

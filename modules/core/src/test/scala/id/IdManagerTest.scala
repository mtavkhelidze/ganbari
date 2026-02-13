package ge.zgharbi.ganbari.core
package id

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.all.*
import org.scalatest.freespec.AsyncFreeSpec

class IdManagerTest extends AsyncFreeSpec with AsyncIOSpec {
  val service = IdManager[IO]

  "make should produce unique IDs" in {
    for {
      id1 <- service.make[String]
      id2 <- service.make[String]
    } yield assert(id1 != id2)
  }

  "from should parse a valid UUID string" in {
    val uuid = java.util.UUID.randomUUID().toString
    service.from[String](uuid).map { id =>
      assert(id.toString == uuid)
    }
  }

  "from should fail on an invalid UUID string" in {
    val invalid = "not-a-uuid"
    service.from[String](invalid).attempt.map {
      case Left(_: IllegalArgumentException) => succeed
      case Left(t)  => fail(s"Unexpected exception: $t")
      case Right(_) => fail("Expected failure for invalid UUID")
    }
  }

  "make should generate distinct IDs across multiple calls" in {
    for {
      ids <- List.fill(10)(service.make[String]).sequence
    } yield assert(ids.distinct.size == ids.size)
  }
}

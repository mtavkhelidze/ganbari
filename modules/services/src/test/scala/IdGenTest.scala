import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{IO, Ref}
import ge.zgharbi.ganbari.services.IdGen
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class IdGenTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "IdGen.uuid[IO]" - {

    val liveGen = IdGen.uuid[IO]

    val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
    val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

    def testGen(ids: UUID*): IO[IdGen[IO, UUID]] = {
      Ref.of[IO, List[UUID]](ids.toList).map { ref =>
        new IdGen[IO, UUID] {
          override def read(s: String): IO[UUID] =
            IO(UUID.fromString(s))

          override def write: IO[UUID] =
            ref.modify {
              case head :: tail => (tail, head)
              case Nil          => throw new NoSuchElementException("exhausted")
            }
        }
      }
    }
    "should generate distinct UUIDs deterministically" in {
      for {
        gen <- testGen(id1, id2)
        a <- gen.write
        b <- gen.write
      } yield {
        a shouldEqual id1
        b shouldEqual id2
        a should not equal b
      }
    }

    "should generate valid UUIDs" in {
      for {
        id1 <- liveGen.write
      } yield {
        id1 shouldBe a[UUID]
      }
    }

    "should parse valid UUID string" in {
      val sample = UUID.randomUUID()
      for {
        parsed <- liveGen.read(sample.toString)
      } yield {
        parsed shouldEqual sample
      }
    }

    "should fail on invalid UUID string" in {
      liveGen.read("not-a-uuid").attempt.map {
        case Left(_: IllegalArgumentException) => succeed
        case _ => fail("Expected IllegalArgumentException")
      }
    }
  }
}

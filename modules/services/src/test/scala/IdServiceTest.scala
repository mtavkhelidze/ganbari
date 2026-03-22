package services
import cats.data.Kleisli
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{IO, Ref}
import org.scalatest.Assertion
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.reflect.ClassTag

class IdServiceTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  // @todo: move ofType into separate test-utils module
  def ofType[E: ClassTag](e: E): Assertion =
    e.isInstanceOf[E] shouldBe true

  val service = IdService.uuid[IO]

  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

  def testGen(ids: UUID*): IO[IdService[IO, UUID]] =
    Ref.of[IO, List[UUID]](ids.toList).map { ref =>
      new IdService[IO, UUID] {
        override def read: Kleisli[IO, String, UUID] =
          Kleisli(s => IO(UUID.fromString(s)))

        override def write: Kleisli[IO, Unit, UUID] =
          Kleisli.liftF(ref.modify {
            case head :: tail => (tail, head)
            case Nil          => throw new NoSuchElementException("exhausted")
          })
      }
    }

  "IdService.uuid[IO]" - {
    // @misha: just to make sure no funny business happens along the way
    "should generate distinct UUIDs deterministically" in {
      for {
        gen <- testGen(id1, id2)
        a <- gen.write.run(())
        b <- gen.write.run(())
      } yield {
        a shouldEqual id1
        b shouldEqual id2
        a should not equal b
      }
    }

    "should generate valid UUIDs" in {
      service.write.run(()) map (_.isInstanceOf[UUID] shouldBe true)
    }

    "should parse valid UUID string" in {
      val sample = UUID.randomUUID()
      service.read.map(_ shouldEqual sample).run(sample.toString)
    }

    "should fail on invalid UUID string" in {
      service.read
        .assertThrowsError(ofType[IllegalArgumentException])
        .run("wrong uuid")
    }
  }
}

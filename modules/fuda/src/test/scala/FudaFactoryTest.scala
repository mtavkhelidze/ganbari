package fuda
import cats.data.Kleisli
import cats.data.Kleisli.*
import cats.effect.*
import cats.syntax.all.*
import fuda.{*, given}
import munit.Location

import java.util.UUID

val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

trait UuidFudaTest extends SuitonoKaizen {
  opaque type UserId = UuidFuda
  val factory = FudaFactory[IO]

  tesuto("should generate a unique ids") {
    (factory.make[UserId] product factory.make[UserId])
      .map { case (i1, i2) => assertNotEquals(i1, i2) }
  }

  tesuto("should parse valid UUID string") {
    val sample = UUID.randomUUID()
    factory
      .restore[UserId]
      .local(_ => sample.toString)
      .map { case id => assertEquals(id.value, sample) }
  }
  tesuto("should generate valid UUIDs") {
    factory.make[UserId].map(id => assert(id.value.isInstanceOf[UUID]))
  }

  tesuto("should fail on invalid UUID string") {
    factory
      .restore[UserId]
      .run("not a UUID")
      .intercept[IllegalArgumentException]
  }
}

class FudaFactoryTest extends SuitonoKaizen("UuidFuda") with UuidFudaTest

//  def testGen(ids: UUID*): IO[IdService[IO, UUID]] =
//    Ref.of[IO, List[UUID]](ids.toList).map { ref =>
//      new IdService[IO, UUID] {
//        override def restore: Kleisli[IO, String, UUID] =
//          Kleisli(s => IO(UUID.fromString(s)))
//
//        override def write: Kleisli[IO, Unit, UUID] =
//          Kleisli.liftF(ref.modify {
//            case head :: tail => (tail, head)
//            case Nil          => throw new NoSuchElementException("exhausted")
//          })
//      }
//    }
//
//  "FudaService.uuid[IO]" - {
//    // @misha: just to make sure no funny business happens along the way
//    "should generate distinct UUIDs deterministically" in {
//      for {
//        gen <- testGen(id1, id2)
//        a <- gen.write.run(())
//        b <- gen.write.run(())
//      } yield {
//        a shouldEqual id1
//        b shouldEqual id2
//        a should not equal b
//      }
//    }
//

//    "should parse valid UUID string" in {
//      val sample = UUID.randomUUID()
//      service.restore.map(_ shouldEqual sample).run(sample.toString)
//    }
//
//    "should fail on invalid UUID string" in {
//      service.restore
//        .assertThrowsError(ofType[IllegalArgumentException])
//        .run("wrong uuid")
//    }

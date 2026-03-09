package keisli

import base.IdType
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.all.*
import kleisli.IdOps
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

private object UserId { sealed trait Tag }
type UserId = IdType[UserId.Tag]

private object TaskId { sealed trait Tag }
type TaskId = IdType[TaskId.Tag]

class IdOpsTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "IdFactory@uuid" - {
    val factory = IdOps.uuid[IO]

    "creates random Id and Id read string" in {
      for {
        uid <- factory.make[UserId]
        tid <- factory.read[TaskId](uid.toString)
      } yield uid === tid shouldBe true
    }

    "generate distinct UUIDs" in {
      for {
        id1 <- factory.make[UserId]
        id2 <- factory.make[UserId]
      } yield {
        id1 =!= id2 shouldBe true
      }
    }

    "fail to create read invalid string" in {
      factory.read[UserId]("not-a-uuid").attempt.map { result =>
        result.isLeft shouldBe true
      }
    }

    "type-safety between phantoms" in {
      assertCompiles("def acceptUid(uid: UserId) = ()")
      assertDoesNotCompile("def acceptUid(uid: TaskId) = acceptUid(uid)")
    }

    "generate many unique IDs" in {
      val n = 1000
      (0 until n).toList.traverse(_ => factory.make[UserId]).map { ids =>
        ids.distinct.size shouldEqual n
      }
    }
  }
}

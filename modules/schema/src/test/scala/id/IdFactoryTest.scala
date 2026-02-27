package schema
package id

import cats.*
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.all.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

private object UserId { sealed trait Tag }
type UserId = IdType[UserId.Tag]

private object TaskId { sealed trait Tag }
type TaskId = IdType[TaskId.Tag]

class IdFactoryTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "#uuid" - {
    "creates random Id and Id from string" in {
      for {
        uid <- IdFactory.uuid[IO].make[UserId]
        tid <- IdFactory.uuid[IO].from[TaskId](uid.toString)
      } yield (uid === tid shouldBe true)
    }

    "round-trip via IdIso" in {
      for {
        uid <- IdFactory.uuid[IO].make[UserId]
      } yield {
        val raw = summon[IdIso[UUID, UserId]].to(uid)
        val recreated = summon[IdIso[UUID, UserId]].from(raw)
        recreated === uid shouldBe true
      }
    }

    "generate distinct UUIDs" in {
      for {
        id1 <- IdFactory.uuid[IO].make[UserId]
        id2 <- IdFactory.uuid[IO].make[UserId]
      } yield {
        id1 =!= id2 shouldBe true
      }
    }

    "fail to create from invalid string" in {
      IdFactory.uuid[IO].from[UserId]("not-a-uuid").attempt.map { result =>
        result.isLeft shouldBe true
      }
    }

    "type-safety between phantoms" in {
      assertCompiles("def acceptUid(uid: UserId) = ()")
      assertDoesNotCompile("def acceptUid(uid: TaskId) = acceptUid(uid)")
    }

    "generate many unique IDs" in {
      val n = 1000
      (0 until n).toList.traverse(_ => IdFactory.uuid[IO].make[UserId]).map {
        ids =>
          ids.distinct.size shouldEqual n
      }
    }
  }
}

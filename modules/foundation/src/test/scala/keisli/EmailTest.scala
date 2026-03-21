package kleisli

import cats.*
import cats.data.Kleisli
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.all.*
import org.scalatest.Assertion
import org.scalatest.Inspectors.forAll
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class EmailTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  def assertThrowsAll(es: List[Email.ErrorMsg]): Throwable => Assertion =
    (t: Throwable) => forAll(es)(hasMessage(_)(t))

  def hasMessage(m: Email.ErrorMsg): Throwable => Assertion =
    (e: Throwable) => e.getMessage should include(m.toString)

  def testOk[F[_]](expected: Email)(using
      ae: ApplicativeError[F, Throwable],
  ): Kleisli[F, Email, Assertion] = Kleisli { e => (e shouldBe expected).pure }

  "Email.unit[IO]" - {
    "should succeed for a valid email" in {
      val expected = Email.from("test@example.com")
      (Email[IO] >>> testOk[IO](expected)).run(expected.value)
    }

    "should fail when @ is missing" in {
      val input = "testexample.com"
      Email[IO]
        .assertThrowsError(hasMessage(Email.ErrorMsg.NoAt))
        .run(input)
    }

    "should fail when dot is missing" in {
      val input = "test@example"
      Email[IO]
        .assertThrowsError(hasMessage(Email.ErrorMsg.NoDot))
        .run(input)
    }

    "should fail when email is empty and accumulate errors" in {
      val input = ""
      Email[IO]
        .assertThrowsError(
          assertThrowsAll(
            List(
              Email.ErrorMsg.Empty,
              Email.ErrorMsg.NoAt,
              Email.ErrorMsg.NoDot,
            ),
          ),
        )
        .run(input)
    }

    "should produce different emails for different valid inputs" in {
      val e1 = Email[IO]("user1@example.com")
      val e2 = Email[IO]("user2@example.com")

      (e1, e2).mapN { (a, b) =>
        a should not equal b
      }
    }

  }

}

package kleisli

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class EmailTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "Email.unit[IO]" - {
    "should succeed for a valid email" in {
      val input = "test@example.com"

      Email[IO](input).attempt.map {
        case Right(email) =>
          email.value shouldBe input
        case Left(err) =>
          fail(s"Expected success but got error: $err")
      }
    }

    "should fail when @ is missing" in {
      val input = "testexample.com"

      Email[IO](input).attempt.map {
        case Right(_) =>
          fail("Expected validation failure")
        case Left(err) =>
          err.getMessage should include("missing @")
      }
    }

    "should fail when dot is missing" in {
      val input = "test@example"

      Email[IO](input).attempt.map {
        case Right(_) =>
          fail("Expected validation failure")
        case Left(err) =>
          err.getMessage should include("missing dot")
      }
    }

    "should fail when email is empty and accumulate errors" in {
      val input = ""

      Email[IO](input).attempt.map {
        case Right(_) =>
          fail("Expected validation failure")
        case Left(err) =>
          err.getMessage should include("missing @")
          err.getMessage should include("missing dot")
          err.getMessage should include("is empty")
      }
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

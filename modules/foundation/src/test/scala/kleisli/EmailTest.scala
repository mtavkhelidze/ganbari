package kleisli

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import cats.syntax.apply.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import kleisli.Email

class EmailTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "should create a valid email successfully" in {
    val validEmail = "test@example.com"
    Email[IO](validEmail).attempt.map {
      case Right(email) => email.value shouldEqual validEmail
      case Left(_) => fail("Expected valid email to be created successfully")
    }
  }

  "should fail to create an invalid email" in {
    val invalidEmail = "invalid-email"
    Email[IO](invalidEmail).attempt.map {
      case Right(_)    => fail("Expected invalid email creation to fail")
      case Left(error) => error shouldBe a[IllegalArgumentException]
    }
  }

  "should create distinct emails for different valid inputs" in {
    val e1 = Email[IO]("user1@example.com")
    val e2 = Email[IO]("user2@example.com")
    (e1, e2).mapN { (e1, e2) => e1 =!= e2 shouldBe true }
  }
}

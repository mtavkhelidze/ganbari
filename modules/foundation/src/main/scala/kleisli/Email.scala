package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import tools.Validations.*

opaque type Email <: String = String

object Email {
  extension (e: Email) def value: String = e

  given Eq[Email] = Eq.by[Email, String](_.value)

  given Show[Email] = Show.show[Email](_.value)

  def apply[F[_]](s: String)(using ApplicativeError[F, Throwable]) =
    program.run(s)

  def program[F[_]](using ae: ApplicativeError[F, Throwable]) =
    Kleisli[F, String, Email] {
      validate(_) match {
        case Validated.Valid(email) => email.pure
        case Validated.Invalid(es)  =>
          IllegalArgumentException(es.mkString(", ")).raiseError
      }
    }

  def validate(s: String): Validated[List[String], Email] =
    runValidators(Checks.all)(s).map(_ => Email.of(s))

  private def of(c: String): Email = c

  private object Checks {
    val all: List[Validator[String]] = List(hasAt, hasDot, isNotEmpty)

    def hasDot: Validator[String] =
      validatorFromPredicate[String](_.contains("."))("missing dot")
    def hasAt: Validator[String] =
      validatorFromPredicate[String](_.contains("@"))("missing @")
    def isNotEmpty: Validator[String] =
      validatorFromPredicate[String](_.nonEmpty)("is empty")

  }
}

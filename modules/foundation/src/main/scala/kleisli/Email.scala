package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import tools.Validations.*

opaque type Email <: String = String

object Email {
  extension (e: Email) def value: String = e

  given Eq[Email] = (a, b) => a.value.equalsIgnoreCase(b)

  given Show[Email] = Show.show[Email](_.value)

  def apply[F[_]](using ae: ApplicativeError[F, Throwable]) =
    Kleisli[F, String, Email] {
      validate(_) match {
        case Validated.Valid(email) => email.pure
        case Validated.Invalid(es)  =>
          IllegalArgumentException(es.mkString(", ")).raiseError
      }
    }

  def validate(s: String): Validated[List[String], Email] =
    runValidators(Checks.all)(s).map(_ => Email.from(s))

  private[kleisli] def from(c: String): Email = c

  private[kleisli] enum ErrorMsg(m: String) {
    override def toString: String = m

    case NoAt extends ErrorMsg("missing @")
    case NoDot extends ErrorMsg("missing dot")
    case Empty extends ErrorMsg("is empty")
  }

  private[kleisli] object Checks {
    val all: List[Validator[String]] = List(hasAt, hasDot, isNotEmpty)

    def hasDot: Validator[String] =
      validatorFromPredicate[String](_.contains("."))(ErrorMsg.NoDot.toString)

    def hasAt: Validator[String] =
      validatorFromPredicate[String](_.contains("@"))(ErrorMsg.NoAt.toString)

    def isNotEmpty: Validator[String] =
      validatorFromPredicate[String](_.nonEmpty)(ErrorMsg.Empty.toString)
  }
}

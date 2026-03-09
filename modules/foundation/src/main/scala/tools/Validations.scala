package tools

import cats.data.*
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.toTraverseOps
object Validations {

  type Validator[A] = A => Validated[String, Unit]

  def runValidators[A](
      vs: List[Validator[A]],
  ): A => Validated[List[String], Unit] = {
    (a: A) =>
      vs.traverse(fn => fn(a).toValidatedNel)
        .leftMap(_.toList)
        .map(_ => ())
  }

  def validatorFromPredicate[A](f: A => Boolean): String => Validator[A] =
    (e: String) => (a: A) => if f(a) then Valid(()) else Invalid(e)
}

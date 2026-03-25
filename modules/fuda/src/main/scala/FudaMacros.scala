package fuda

import cats.*

import java.util.UUID
import scala.quoted.*

object FudaMacros {
  def value[A: Type](a: Expr[A])(using Quotes): Expr[?] = {
    import quotes.reflect.*

    def make[R: Type](x: Expr[A]): Expr[R] = '{ $x.asInstanceOf[R] }

    TypeRepr.of[A].dealias match {
      case und if und =:= TypeRepr.of[UuidFuda] => make[UUID](a)
//      case und if und =:= TypeRepr.of[LongFuda] => make[Long](a)
      case und                                  =>
        report.errorAndAbort(
          s"Cannot derive Eq for ${Type.show[A]}. " +
            s"Underlying type ${und.show} is not a known Fuda type.",
        )
    }

  }
  def eq[A: Type](using Quotes): Expr[Eq[A]] = {
    import quotes.reflect.*

    def make[R: Type](eq: Expr[Eq[R]]): Expr[Eq[A]] =
      '{
        new Eq[A] {
          def eqv(x: A, y: A): Boolean =
            x.asInstanceOf[R].equals(y.asInstanceOf[R])
        }
      }

    def summonEq[R: Type]: Expr[Eq[R]] =
      Expr
        .summon[Eq[R]]
        .getOrElse(report.errorAndAbort(s"Cannot find Eq[${Type.show[R]}]"))

    TypeRepr.of[A].dealias match {
      case und if und =:= TypeRepr.of[UuidFuda] => make[UUID](summonEq[UUID])
//      case und if und =:= TypeRepr.of[LongFuda] => make[Long](summonEq[Long])
      case und                                  =>
        report.errorAndAbort(
          s"Cannot derive Eq for ${Type.show[A]}. " +
            s"Underlying type ${und.show} is not a known Fuda type.",
        )
    }
  }
}

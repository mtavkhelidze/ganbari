package fuda

import cats.*
import cats.data.*
import cats.implicits.*

import java.util.UUID
import scala.annotation.implicitNotFound
import scala.quoted.*

private[fuda] trait FudaMaker[U] {
  def make[F[_]: ApplicativeThrow](using UuidMaker[F]): Kleisli[F, Unit, U]
}

private[fuda] trait FudaReader[U] {
  def read[F[_]: ApplicativeThrow](using UuidReader[F]): Kleisli[F, String, U]
}

@implicitNotFound(
  "User type ${U} is not Fuda.Type. " +
    "Correct declaration: opaque MyType <: Fuda.Type = Fuda.Type",
)
trait Fuda[U] extends FudaMaker[U] with FudaReader[U]

object Fuda {
  opaque type Type <: UUID = UUID

  inline given instance[U](using U <:< Fuda.Type): Fuda[U] = {
    ${ FudaMacros.instanceMacro[U] }
  }

  def apply[U](using f: Fuda[U]): Fuda[U] = f
}

private[fuda] object FudaMacros {
  def instanceMacro[U: Type](using q: Quotes): Expr[Fuda[U]] = {
    '{
      new Fuda[U] {
        private def toType: UUID => U = _.asInstanceOf[U]

        override def make[F[_]: ApplicativeThrow](using
            svc: UuidMaker[F],
        ): Kleisli[F, Unit, U] =
          Kleisli(_ => svc.make.map(toType))

        override def read[F[_]: ApplicativeThrow](using
            svc: UuidReader[F],
        ): ReaderT[F, String, U] =
          Kleisli(svc.read(_).map(toType))
      }
    }
  }
}

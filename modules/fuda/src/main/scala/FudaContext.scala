package fuda
import cats.effect.*

import java.util.UUID
import scala.quoted.*

trait FudaContext[F[_], A] {
  private[fuda] type Und
  private[fuda] val svc: FudaService[F, Und]
  private[fuda] def from(u: Und): A
}

object FudaContext {
  def derivedMacro[F[_]: Type, A: Type](using
      quotes: Quotes,
  ): Expr[FudaContext[F, A]] = {
    import quotes.reflect.*

    inline def notFudaType =
      quotes.reflect.report.errorAndAbort(
        s"Unknown Fuda type: ${Type.show[A]}",
      )

    inline def svcExpr[Raw: Type]: Expr[FudaService[F, Raw]] = {
      Expr.summon[FudaService[F, Raw]].getOrElse {
        report.errorAndAbort(
          s"No implicit FudaService[F, ${Type.show[Raw]}] in scope",
        )
      }
    }
    inline def makeContext[Raw: Type]: Expr[FudaContext[F, A]] =
      '{
        new FudaContext[F, A] {
          override type Und = UUID
          override val svc: FudaService[F, UUID] = ${ svcExpr[UUID] }

          override def from(u: UUID): A = u.asInstanceOf[A]
        }
      }

    TypeRepr.of[A].dealias match {
      case und if und =:= TypeRepr.of[UuidFuda] => makeContext[UUID]
      case _ => notFudaType
    }
  }
}

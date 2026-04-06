package middle
package kiroku

import back.storage.{KirokuRow, KirokuWriter}
import cats.*
import cats.data.*
import cats.effect.std.Console
import cats.implicits.*
import foundation.kleisli.Kiroku

// Roughly modelled after syslog(3)
trait KirokuShoribu[F[_]: MonadThrow] {
  def syslog(using KirokuWriter[F]): Kleisli[F, Kiroku, Unit]
}

object KirokuShoribu {
  import middle.kiroku.ops.KirokuOps.*

  def apply[F[_]: MonadThrow]: KirokuShoribu[F] = new {
    def syslog(using w: KirokuWriter[F]): Kleisli[F, Kiroku, Unit] =
      Kleisli.ask[F, Kiroku].flatMap(_.toRow) andThen w.write
  }

  def apply[F[_]: {MonadThrow, Console}](opts: WriteTo): KirokuShoribu[F] =
    new {
      def syslog(using w: KirokuWriter[F]): Kleisli[F, Kiroku, Unit] =
        Kleisli.ask[F, Kiroku] andThen yell(opts).flatMap(
          _.toRow,
        ) andThen w.write
    }

  private def yell[F[_]: {MonadThrow, Console}] = (opts: WriteTo) =>
    Kleisli[F, Kiroku, Kiroku](k =>
      opts match
        case WriteTo.Stdout => Console[F].println(k.toString).as(k)
        case WriteTo.StderrorAlso => Console[F].error(k.toString).as(k)
        case _ => k.pure,
    )

  enum WriteTo {
    case JustBackend

    /** No backend, just console logging. Userfull for debugging, but defies the
      * concept of even sourcing since nothing is persisted.
      */
    case Stdout

    /** Use backend, but also output to /dev/stderror.
      */
    case StderrorAlso
  }
}

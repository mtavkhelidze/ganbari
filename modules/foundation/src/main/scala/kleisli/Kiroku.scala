package foundation
package kleisli

import cats.*
import cats.data.*
import cats.implicits.*
import cats.syntax.all.*
import fuda.*

opaque type KirokuId <: Fuda.Id = Fuda.Id

enum Kiroku(id: KirokuId, on: Nichiji) {
  case Koutou private[Kiroku] (id: KirokuId, on: Nichiji) extends Kiroku(id, on)
}

object Kiroku {
  def unit[F[_]: MonadThrow]: Kleisli[F, Nichiji, Kiroku] = {
    Kleisli
      .pure(Koutou.apply.curried)
      .ap(Fuda[KirokuId].make[F].lmap(_ => ()))
      .ap(Kleisli.ask[F, Nichiji])
  }

  object Koutou {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Kiroku] = unit[F]
  }
}

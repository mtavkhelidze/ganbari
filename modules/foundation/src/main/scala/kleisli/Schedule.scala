package foundation
package kleisli

import kenshou.NichijiInput

import cats.*
import cats.data.*
import cats.implicits.*
import cats.syntax.all.*

enum Schedule(val start: Nichiji) {
  case Daily private (override val start: Nichiji) extends Schedule(start)
  case EveryGiven private (val day: Nichiji) extends Schedule(day)
  case Fortnight private (override val start: Nichiji) extends Schedule(start)
  case Monthly private (override val start: Nichiji) extends Schedule(start)
  case Workday private (override val start: Nichiji) extends Schedule(start)
}

object Schedule {
  private def lift[F[_]: Applicative, A](
      f: Nichiji => Schedule,
  ): Kleisli[F, Nichiji, Schedule] =
    Kleisli(n => f(n).pure[F])

  object Daily {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = lift(
      new Daily(_),
    )
  }

  object EveryGiven {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = lift {
      new EveryGiven(_)
    }
  }

  object Fortnight {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = lift {
      new Fortnight(_)
    }
  }

  object Monthly {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] =
      NichijiInput.isViableMonthlyDay[F].map(new Schedule.Monthly(_))
  }

  object Workday {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = lift {
      new Schedule.Workday(_)
    }
  }
}

package foundation
package kleisli

import meta.KleisliApply

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*

enum Schedule(val start: Nichiji) {
  case Daily private (override val start: Nichiji) extends Schedule(start)
  case EveryGivenDay private (override val start: Nichiji)
      extends Schedule(start)
  case Fortnight private (override val start: Nichiji) extends Schedule(start)
  case Monthly private (override val start: Nichiji) extends Schedule(start)
  case Workday private (override val start: Nichiji) extends Schedule(start)
}

object Schedule {
  object Daily {
    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = Kleisli {
      new Schedule.Daily(_).pure[F]
    }
  }
}

import Schedule.*

val x: ReaderT[IO, Nichiji, Schedule] = Daily[IO]
//
//  object EveryGivenDay {
//    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = Kleisli {
//      new Schedule.EveryGivenDay(_).pure[F]
//    }
//  }
//
//  object Fortnight {
//    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = Kleisli {
//      new Schedule.Fortnight(_).pure[F]
//    }
//  }
//
//  object Monthly {
//    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] =
//      NichijiInput.isViableMontlyDay[F].map(new Schedule.Monthly(_))
//  }
//
//  object Workday {
//    def apply[F[_]: MonadThrow]: Kleisli[F, Nichiji, Schedule] = Kleisli {
//      new Schedule.Workday(_).pure[F]
//    }
//  }
//}

package foundation
package kleisli

import cats.*
import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.freespec.AsyncFreeSpec

class DomainTest
    extends AsyncFreeSpec
    with AsyncIOSpec
    with TypeCheckedTripleEquals {

  "invalid name fails" in {
    Domain[IO]
      .assertThrows[IllegalArgumentException]
      .run("")
  }

  "happy path with id" in {
    Domain[IO]
      .map(d =>
        assert(
          d.name === "@health" && d.id.isInstanceOf[DomainId],
        ),
      )
      .run("@health")
  }
}

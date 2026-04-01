package exp
trait SpecialApply[T] {
  def apply(param: String, param2: String): T
}

sealed trait SealedTest(param: String)
case class TestOne(param: String) extends SealedTest(param)
case class TestTwo(param: String) extends SealedTest(param)

object SealedTest {
  object Make {
    Macropathya.makeFor[SealedTest]
  }
}

@main def oteherMain(): Unit = {
  import SealedTest.*
//  val x = TestOne("Misha", "Tavkhelidze")
//  val y = TestTwo("Other", "Name")
//  println(x)
//  println(y)
}

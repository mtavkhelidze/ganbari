package fuda

import munit.CatsEffectSuite

trait SuitonoKaizen(tag: String) extends CatsEffectSuite {
  def tesuto(msg: String) = test(s"$tag: $msg")
}

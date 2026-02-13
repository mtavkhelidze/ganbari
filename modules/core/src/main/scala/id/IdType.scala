package ge.zgharbi.ganbari.core
package id

import cats.syntax.show.*
import cats.{Eq, Show}

import java.util.UUID

opaque type IdType[A] = UUID

object IdType {
  def apply[A](uuid: UUID): IdType[A] = uuid

  extension [A](id: IdType[A]) def value: UUID = id

  implicit def eqInstance[A]: Eq[IdType[A]] = Eq.by(_.value)

  implicit def showInstance[A]: Show[IdType[A]] = Show.show(id => id.value.show)

  implicit def orderingInstance[A]: Ordering[IdType[A]] = Ordering.by(_.value)
}

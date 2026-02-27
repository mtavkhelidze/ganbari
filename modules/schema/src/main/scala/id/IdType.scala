package schema
package id

import cats.*

import java.util.UUID

opaque type IdType[A] = Any

trait IdIso[Raw, A] {
  def from(a: Raw): A
  def to(a: A): Raw
}

object IdType {
  given [A]: Eq[IdType[A]] = (a, b) => a == b

  given uuid[A]: IdIso[UUID, IdType[A]] with {
    def from(a: UUID): IdType[A] = a.asInstanceOf[A]
    def to(a: IdType[A]): UUID = a.asInstanceOf[UUID]
  }

  given long[A]: IdIso[Long, IdType[A]] with {
    def from(a: Long): IdType[A] = a.asInstanceOf[A]
    def to(a: IdType[A]): Long = a.asInstanceOf[Long]
  }
}

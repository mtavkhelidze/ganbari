package kleisli

import cats.data.*
import cats.effect.*

import java.util.UUID

// Generic Id type
type Fuda[A] = Any

// Concrete one
object MishaId { sealed trait Tag }
opaque type MishaId = Fuda[MishaId.Tag]

// Connection between named id type and raw system/hhatever type
trait Iso[Raw, A: Fuda] {
  def from(a: Raw): A
  def to(a: A): Raw
}

// factory
trait IdFactory[F[_]: Sync] {
  def make[A: Fuda, Raw](using iso: Iso[Raw, A]): Kleisli[F, Unit, A]
}

// implementations
given uuid[A: Fuda]: Iso[UUID, A] = new Iso {
  override def from(a: UUID): A = a.asInstanceOf[A]

  override def to(a: A): UUID = a.asInstanceOf[UUID]
}

trait IdType[F[_]: Sync] extends IdFactory[F] {
  override def make[A: Fuda, Raw](using
      iso: Iso[Raw, A],
  ): ReaderT[F, Unit, A] = ???
}

// use
val x: IO[MishaId] = IdType[IO].make[MishaId, UUID].run(())

//  inline def service[F[_]: Sync, Raw] = inline erasedValue[Raw] match {
//    case _: UUID => summon[IdService[F, UUID]]
//  }
//
//  inline def iso[A, Raw] = inline erasedValue[Raw] match {
//    case _: UUID => summon[Iso[UUID, A]]
//    case _: Long => summon[Iso[Long, A]]
//  }
//
//  given [A]: Iso[UUID, A] = new Iso[UUID, A] {
//    def from(a: UUID): A = a.asInstanceOf[A]
//
//    def to(a: A): UUID = a.asInstanceOf[UUID]
//  }
//
////  override def read[A, Raw]: Kleisli[F, String, IdType[A]] = ???
//
//  inline given [A]: Iso[Long, A] = new Iso[Long, A] {
//    def from(a: Long): A = a.asInstanceOf[A]
//    def to(a: A): Long = a.asInstanceOf[Long]
//  }
//
//  private inline def pickIso[Raw, A] = inline erasedValue[Raw] match {
//    case _: UUID => summon[Iso[UUID, A]]
////    case _: Long => summon[Iso[Long, A]]
//  }

//  @implicitNotFound(
//    "Cannot find Iso instance from ${Raw} to IdType[${A}]. Did you forget to import IdType.given?",
//  )

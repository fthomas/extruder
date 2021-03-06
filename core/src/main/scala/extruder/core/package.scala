package extruder

import cats.data.{NonEmptyList, ValidatedNel}
import cats.effect.IO

package object core {
  implicit object `decode disambiguator`
  implicit object `decode without input disambiguator`

  type EitherThrowable[T] = Either[Throwable, T]
  type EitherErrors[T] = Either[ValidationErrors, T]
  type ValidationErrors = NonEmptyList[ValidationError]
  type Validation[T] = ValidatedNel[ValidationError, T]
  type IOF[F[_], T] = IO[F[T]]

  val TypeKey: String = "type"
}

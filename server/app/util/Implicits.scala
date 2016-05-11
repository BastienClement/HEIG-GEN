package util

import scala.concurrent.Future
import scala.language.implicitConversions

object Implicits {
	implicit def futureWrapper[T](value: T): Future[T] = Future.successful(value)
	implicit def optionWrapper[T](value: T): Option[T] = Option(value)
}

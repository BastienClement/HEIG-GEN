package controllers.api

import _root_.util.Implicits.futureWrapper
import _root_.util.{Crypto, DateTime}
import play.api.Configuration
import play.api.http.Writeable
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * API request with user ID and admin flag
  *
  * @param user    the user's ID
  * @param admin   the user global-admin flag
  * @param request the original request
  * @tparam A the body type of the request
  */
case class ApiRequest[A](user: Int, admin: Boolean, request: Request[A]) extends WrappedRequest[A](request)

/**
  * Mixin trait for API controllers.
  * The controller must be injected with conf and an execution context.
  */
trait ApiActionBuilder {
	implicit val conf: Configuration
	implicit val ec: ExecutionContext

	/**
	  * Serializes Throwable instance into JSON objects.
	  * A null Throwable is serialized to JsNull.
	  */
	private[this] def serializeThrowable(t: Throwable, seen: Set[Throwable] = Set()): JsValue = {
		if (t == null || seen.contains(t)) JsNull
		else Json.obj(
			"class" -> t.getClass.getName,
			"message" -> t.getMessage,
			"trace" -> Try { t.getStackTrace.map(_.toString): JsValueWrapper }.getOrElse(Json.arr()),
			"cause" -> serializeThrowable(t, seen + t)
		)
	}

	implicit val throwableWrites: Writes[Throwable] = Writes[Throwable](serializeThrowable(_))

	/** Error message JS object */
	val writeSymbol: (Symbol => JsObject) = sym => Json.obj("err" -> sym.name)

	implicit val errorSymbol = Writeable[Symbol](
		writeSymbol.andThen(implicitly[Writeable[JsValue]].transform),
		Some("application/json")
	)

	implicit class WithCause(val sym: Symbol) {
		def withCause(t: Throwable): JsObject = writeSymbol(sym) + ("cause" -> serializeThrowable(t))
	}

	/** Safely invoke the action constructor and catch potential exceptions */
	private def wrap[R, A](block: R => Future[Result]): R => Future[Result] = {
		(req: R) => {
			try {
				block(req)
			} catch {
				case err: Throwable => InternalServerError('UNCAUGHT_EXCEPTION.withCause(err))
			}
		}
	}

	/** An authenticated user action */
	object UserAction extends ActionBuilder[ApiRequest] {

		/** Builds a new ApiRequest from a verified token */
		def build[A](token: JsObject)(implicit request: Request[A]) =
			ApiRequest((token \ "user").as[Int], (token \ "admin").as[Boolean], request)

		/** Check that the token expire date is not in the past */
		def checkExpires(token: JsObject): Boolean =
			(token \ "expires").asOpt[DateTime].exists(_ > DateTime.now)

		/** Transforms a basic Request to ApiRequest */
		def transform[A](implicit request: Request[A]) =
			request.headers.get("X-Auth-Token").flatMap(Crypto.check).filter(checkExpires).map(build[A] _)

		/** Failure to authenticate */
		def failure = Future.successful(Unauthorized(JsNull))

		/** Invoke the action's block */
		override def invokeBlock[A](request: Request[A], block: ApiRequest[A] => Future[Result]) =
			transform(request).map(wrap(block)).getOrElse(failure)
	}

	/** Safely perform unauthenticated request */
	object UnauthenticatedApiAction extends ActionBuilder[Request] {
		override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = wrap(block)(request)
	}

	/** A placeholder for not implemented actions */
	def NotYetImplemented = Action { req => NotImplemented('NOT_YET_IMPLEMENTED) }
}

package controllers.api

import play.api.Configuration
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import util.{Crypto, DateTime}
import util.Implicits.futureWrapper

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

	/** An authenticated user action */
	object UserAction extends ActionBuilder[ApiRequest] {
		/** Type of the Request handler block */
		type RequestHandler[A] = (ApiRequest[A]) => Future[Result]

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

		/** Safely invoke the action constructor and catch potential exceptions */
		def wrap[A](block: RequestHandler[A]): RequestHandler[A] = (req: ApiRequest[A]) => {
			try {
				block(req)
			} catch {
				case err: Throwable =>
					def serialize(e: Throwable): JsValue = {
						if (e == null) JsNull
						else Json.obj(
							"class" -> e.getClass.getName,
							"message" -> e.getMessage,
							"trace" -> Try { e.getStackTrace.map(_.toString): JsValueWrapper }.getOrElse(Json.arr()),
							"cause" -> serialize(e.getCause)
						)
					}

					InternalServerError(Json.obj(
						"err" -> "UNCAUGHT_EXCEPTION",
						"exception" -> serialize(err)
					))
			}
		}

		/** Invoke the action's block */
		override def invokeBlock[A](request: Request[A], block: RequestHandler[A]) =
			transform(request).map(wrap(block)).getOrElse(failure)
	}

	/** A placeholder for not implemented actions */
	def NotYetImplemented = Action { req =>
		NotImplemented(Json.obj("err" -> "Not yet implemented"))
	}
}

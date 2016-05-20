package controllers.api

import play.api.Configuration
import play.api.libs.json.{JsNull, JsObject}
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, Request, Result, WrappedRequest}
import scala.concurrent.{ExecutionContext, Future}
import util.{Crypto, DateTime}

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
		override def invokeBlock[A](request: Request[A], block: (ApiRequest[A]) => Future[Result]) =
			transform(request).map(block).getOrElse(failure)
	}
}

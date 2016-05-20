package controllers

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsNull, JsObject, Json}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import util.DateTime.Units
import util.{Crypto, DateTime}
import play.api.mvc.Results.Unauthorized
import util.Implicits.futureWrapper

case class ApiRequest[A](user: Int, admin: Boolean, request: Request[A]) extends WrappedRequest[A](request)

trait ApiActionBuilder {
	implicit val conf: Configuration
	implicit val ec: ExecutionContext

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

@Singleton
class ApiController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def genToken(implicit user: User): String = {
		val token = Json.obj("user" -> user.id, "admin" -> user.admin, "expires" -> (DateTime.now + 1.year))
		Crypto.sign(token)
	}

	def token = Action.async(parse.json) { req =>
		val user = (req.body \ "user").as[String]
		val pass = (req.body \ "pass").as[String]

		val res = for {
			u <- Users.findByUsername(user).run
			if Crypto.check(pass, u.pass)
		} yield u

		res.map { u =>
			Ok(Json.obj("token" -> genToken(u)))
		}.recover { case e =>
			Unauthorized(Json.obj("err" -> e.getMessage))
		}
	}

	def register = Action.async(parse.json) { req =>
		val user = (req.body \ "user").as[String]
		val pass = (req.body \ "pass").as[String]

		if (user.length < 3) {
			BadRequest(Json.obj("err" -> "REGISTER_NAME_TOO_SHORT"))
		} else if (pass.length < 5) {
			// 5 chars password is quite sad :(
			BadRequest(Json.obj("err" -> "REGISTER_PASS_TOO_SHORT"))
		} else {
			val insert = Users += User(0, user.toLowerCase, Crypto.hash(pass), admin = false)

			insert.run.flatMap { _ =>
				Users.findByUsername(user.toLowerCase).run
			}.map { user =>
				Ok(Json.obj("token" -> genToken(user)))
			}.recover { case _ =>
				Conflict(Json.obj("err" -> "REGISTER_ALREADY_TAKEN"))
			}
		}
	}

	def test = UserAction { req =>
		Ok(req.user.toString)
	}
}

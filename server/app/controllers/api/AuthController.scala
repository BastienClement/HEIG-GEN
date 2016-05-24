package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.ExecutionContext
import util.DateTime.Units
import util.Implicits.futureWrapper
import util.{Crypto, DateTime}

/**
  * Authentication actions
  */
@Singleton
class AuthController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {
	/**
	  * Generates a new authentication token
	  *
	  * Currently generates one-year tokens, because it will be easier
	  * on the client-side to not handle the re-authentication case.
	  *
	  * @param user the user authenticated by the token
	  */
	def genToken(implicit user: User): String = {
		val token = Json.obj("user" -> user.id, "admin" -> user.admin, "expires" -> (DateTime.now + 1.year))
		Crypto.sign(token)
	}

	/**
	  * Request for a new authentication token from credentials
	  */
	def token = UnauthenticatedApiAction.async(parse.json) { req =>
		val user = (req.body \ "user").as[String]
		val pass = (req.body \ "pass").as[String]

		Users.findByUsername(user).run.filter(u => Crypto.check(pass, u.pass)).map { u =>
			Ok(Json.obj("token" -> genToken(u)))
		}.recover { case e =>
			Unauthorized('TOKEN_BAD_CREDENTIALS)
		}
	}

	/**
	  * Account creation request, returns a token for the newly created account
	  * Username and password must be at least 5 chars long.
	  */
	def register = UnauthenticatedApiAction.async(parse.json) { req =>
		val user = (req.body \ "user").as[String]
		val pass = (req.body \ "pass").as[String]

		if (user.length < 3) {
			UnprocessableEntity('AUTH_REGISTER_NAME_TOO_SHORT)
		} else if (pass.length < 5) {
			// 5 chars password is quite sad :(
			UnprocessableEntity('AUTH_REGISTER_PASS_TOO_SHORT)
		} else {
			Users.register(user, pass).run.flatMap { _ =>
				Users.findByUsername(user.toLowerCase).run
			}.map { user =>
				Created(Json.obj("token" -> genToken(user)))
			}.recover { case _ =>
				Conflict('AUTH_REGISTER_NAME_ALREADY_TAKEN)
			}
		}
	}

	def test = UserAction { req =>
		throw new IllegalArgumentException()
	}
}

package controllers

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext
import util.PasswordHasher

@Singleton
class ApiController @Inject()(implicit ec: ExecutionContext) extends Controller {

	def login = Action.async(parse.json) { req =>
		val user = (req.body \ "user").as[String]
		val password = (req.body \ "password").as[String]

		val res = for {
			u <- Users.findByUsername(user).run
			if PasswordHasher.check(password, u.pass)
		} yield Json.obj("res" -> "ok")

		res.map(Ok(_)).recover { case e => Unauthorized(Json.obj("res" -> "nok", "err" -> e.getMessage)) }
	}

	def register = Action.async(parse.json) { req =>
		val username = (req.body \ "user").as[String]
		val password = (req.body \ "password").as[String]

		val insert = Users += User(0, username.toLowerCase, PasswordHasher.hash(password), false)

		insert.run.map {
			case _ => Ok(Json.obj("res" -> "ok"))
		}.recover {
			case _ => Conflict(Json.obj("res" -> "nok", "err" -> "This username is already taken"))
		}
	}
}

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
		val pass = (req.body \ "pass").as[String]

		val res = for {
			u <- Users.findByUsername(user).run
			if PasswordHasher.check(pass, u.pass)
		} yield u

		res.map {
			case u => Ok(Json.obj("res" -> "ok"))
		}.recover {
			case e => Unauthorized(Json.obj("res" -> "nok", "err" -> e.getMessage))
		}
	}

	def register = Action.async(parse.json) { req =>
		val user = (req.body \ "user").as[String]
		val pass = (req.body \ "pass").as[String]

		val insert = Users += User(0, user.toLowerCase, PasswordHasher.hash(pass), admin = false)

		insert.run.map {
			case _ => Ok(Json.obj("res" -> "ok"))
		}.recover {
			case _ => Conflict(Json.obj("res" -> "nok", "err" -> "This username is already taken"))
		}
	}
}

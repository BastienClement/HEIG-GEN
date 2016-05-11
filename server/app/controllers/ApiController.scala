package controllers

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, Controller}
import util.PasswordHasher

@Singleton
class ApiController @Inject()() extends Controller {
	def index = Action { req =>
		Ok(PasswordHasher.hash(req.getQueryString("pass").getOrElse("")))
	}
}

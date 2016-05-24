package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.JsArray
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

@Singleton
class UsersController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def list = UserAction.async {
		Users.sortBy(_.name.asc).run.map { users =>
			Ok(JsArray(users.map(_.toJson)))
		}
	}

	private def fetchUser(id: Int) = {
		Users.filter(_.id === id).headOption.map { user =>
			user.map(u => Ok(u.toJson)).getOrElse(NotFound('USERS_GET_NONEXISTANT))
		}
	}

	def user(id: Int) = UserAction.async(fetchUser(id))
	def self = UserAction.async(req => fetchUser(req.user))

	def delete = NotYetImplemented
}

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
	/**
	  * The list of every registered users.
	  */
	def list = UserAction.async {
		Users.sortBy(_.name.asc).run.map { users =>
			Ok(JsArray(users.map(_.toJson)))
		}
	}

	/**
	  * Fetches a specific user.
	  * @param id the user to fetch
	  */
	private def fetchUser(id: Int) = {
		Users.filter(_.id === id).headOption.map { user =>
			user.map(u => Ok(u.toJson)).getOrElse(NotFound('USERS_GET_NONEXISTANT))
		}
	}

	/**
	  * Information about a given user.
	  * @param id the requested user
	  */
	def user(id: Int) = UserAction.async(fetchUser(id))

	/**
	  * Information about the user himself.
	  */
	def self = UserAction.async(req => fetchUser(req.user))

	/**
	  * List of unknown users for the client.
	  * This list excludes contacts and self.
	  */
	def unknowns = UserAction.async { req =>
		Users.sortBy { u =>
			u.name.asc
		}.filter { u =>
			!(u.id in Contacts.ofUserById(req.user)) && (u.id =!= req.user)
		}.run.map { users =>
			Ok(JsArray(users.map(_.toJson)))
		}
	}

	/**
	  * Deletes a user.
	  */
	def delete = NotYetImplemented
}

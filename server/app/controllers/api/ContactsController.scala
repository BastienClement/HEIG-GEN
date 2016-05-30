package controllers.api

import com.google.inject.{Inject, Singleton}
import java.sql.SQLException
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.JsArray
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import util.Implicits.futureWrapper

@Singleton
class ContactsController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {
	/**
	  * Fetches the contacts list.
	  */
	def list = UserAction.async { req =>
		val request = for {
			contact <- Contacts.filter(_.owner === req.user)
			user <- Users.filter(_.id === contact.user)
		} yield user

		request.run.map { users =>
			Ok(JsArray(users.map(_.toJson)))
		}
	}

	/**
	  * Adds a new user as contact.
	  */
	def add(user: Int) = UserAction.async { req =>
		if (user == req.user) {
			BadRequest('CONTACTS_ADD_SELF)
		} else {
			val query = Contacts += Contact(req.user, user)
			query.run.map { _ =>
				NoContent
			}.recover {
				case e: SQLException if e.getErrorCode == 1452 => UnprocessableEntity('CONTACTS_ADD_NONEXISTANT)
				case e: SQLException if e.getErrorCode == 1062 => Conflict('CONTACTS_ADD_DUPLICATE)
			}
		}
	}

	/**
	  * Deletes an user from contacts.
	  */
	def delete(user: Int) = UserAction.async { req =>
		Contacts.filter(c => c.owner === req.user && c.user === user).delete.run.map { count =>
			if (count > 0) NoContent
			else NotFound('CONTACTS_DELETE_NONEXISTANT)
		}
	}

	/**
	  * Fetches information about a contact.
	  */
	def get(id: Int) = UserAction.async { req =>
		val request = for {
			contact <- Contacts if contact.owner === req.user && contact.user === id
			user <- Users if user.id === id
 		} yield user

		request.headOption.map { user =>
			user.map(u => Ok(u.toJson)).getOrElse(NotFound('CONTACTS_GET_NONEXISTANT))
		}
	}
}

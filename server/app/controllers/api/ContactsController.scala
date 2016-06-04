package controllers.api

import com.google.inject.{Inject, Singleton}
import java.sql.SQLException
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, JsBoolean}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import scala.util.Success
import services.PushService
import util.Implicits.futureWrapper

@Singleton
class ContactsController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration, val push: PushService)
		extends Controller with ApiActionBuilder {
	/**
	  * Fetches the contacts list.
	  */
	def list = UserAction.async { req =>
		Contacts.ofUser(req.user).flatMap { c =>
			val other = Case.If(c.a === req.user).Then(c.b).Else(c.a)
			Users.filter(_.id === other)
		}.map { user =>
			(user, UnreadFlags.contactUnread(req.user, user.id))
		}.run.map { users =>
			Ok(JsArray(users.map { case (user, unread) =>
				user.toJson + ("unread" -> JsBoolean(unread))
			}))
		}
	}

	/**
	  * Adds a new user as contact.
	  */
	def add(user: Int) = UserAction.async { req =>
		if (req.user == user) {
			BadRequest('CONTACTS_ADD_SELF)
		} else {
			Contacts.bind(user, req.user).map { _ =>
				NoContent
			}.recover {
				case e: SQLException if e.getErrorCode == 1452 => NotFound('CONTACTS_ADD_NOT_FOUND)
				case e: SQLException if e.getErrorCode == 1062 => Conflict('CONTACTS_ADD_DUPLICATE)
			}.andThen {
				case Success(_) =>
					push.send(user, 'CONTACT_ADDED, "contact" -> req.user)
					push.send(req.user, 'CONTACT_ADDED, "contact" -> user)
			}
		}
	}

	/**
	  * Deletes an user from contacts.
	  */
	def delete(user: Int) = UserAction.async { req =>
		Contacts.unbind(req.user, user).map { count =>
			if (count > 0) {
				push.send(user, 'CONTACT_REMOVED, "contact" -> req.user)
				push.send(req.user, 'CONTACT_REMOVED, "contact" -> user)
				NoContent
			} else {
				NotFound('CONTACTS_DELETE_NOT_FOUND)
			}
		}
	}

	/**
	  * Fetches information about a contact.
	  */
	def get(id: Int) = UserAction.async { req =>
		val request = for {
			contact <- Contacts.get(req.user, id)
			user <- Users if user.id === id
 		} yield user

		request.headOption.map { user =>
			user.map(u => Ok(u.toJson)).getOrElse(NotFound('CONTACTS_GET_NOT_FOUND))
		}
	}
}

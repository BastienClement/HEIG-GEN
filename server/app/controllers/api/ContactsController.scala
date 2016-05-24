package controllers.api

import com.google.inject.{Inject, Singleton}
import java.sql.SQLException
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.JsArray
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

@Singleton
class ContactsController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def list = UserAction.async { req =>
		val request = for {
			contact <- Contacts.filter(_.owner === req.user)
			user <- Users.filter(_.id === contact.user)
		} yield user

		request.run.map { users =>
			Ok(JsArray(users.map(_.toJson)))
		}
	}

	def add(user: Int) = UserAction.async { req =>
		val query = Contacts += Contact(req.user, user)
		query.run.map { _ =>
			NoContent
		}.recover {
			case e: SQLException if e.getErrorCode == 1452 => UnprocessableEntity('CONTACT_ADD_NONEXISTANT)
			case e: SQLException if e.getErrorCode == 1062 => Conflict('CONTACT_ADD_DUPLICATE)
		}
	}

	def delete(user: Int) = UserAction.async { req =>
		Contacts.filter(c => c.owner === req.user && c.user === user).delete.run.map { count =>
			if (count > 0) NoContent
			else NotFound('CONTACT_DELETE_NONEXISTANT)
		}
	}
}

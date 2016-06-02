package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import scala.util.Success
import services.PushService
import util.DateTime
import util.Implicits.futureWrapper

@Singleton
class PrivateChatsController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration, push: PushService)
		extends Controller with ApiActionBuilder {

	// TODO: check that user is a contact

	/**
	  * Lists message between two users.
	  */
	def list(user: Int) = UserAction.async { req =>
		PrivateMessages.between(user, req.user).sortBy { m =>
			m.date.desc
		}.optMap(req.getQueryStringAsInt("from")) { (q, from) =>
			q.filter(_.id > from)
		}.optMap(req.getQueryStringAsInt("limit")) { (q, limit) =>
			q.take(limit)
		}.run.map { list =>
			Ok(JsArray(list.map(_.toJson)))
		}
	}

	/**
	  * Sends a message to the user.
	  */
	def post(user: Int) = UserAction.async(parse.json) { req =>
		Contacts.bound(user, req.user).flatMap { bound =>
			if (!bound) {
				Forbidden('PRIVATE_CHATS_POST_NOT_A_CONTACT)
			} else {
				val text = (req.body \ "text").as[String]
				val msg = PrivateMessage(0, req.user, user, DateTime.now, text)
				PrivateMessages.insert(msg).run.map { m =>
					Created(Json.obj("id" -> m.id))
				}.andThen {
					case Success(_) =>
						push.send(user, 'PRIVATE_MESSAGES_UPDATED, "contact" -> req.user)
						push.send(req.user, 'PRIVATE_MESSAGES_UPDATED, "contact" -> user)
						UnreadFlags.setContactUnread(user, req.user)
				}
			}
		}
	}

	def read(user: Int) = UserAction { req =>
		UnreadFlags.setContactRead(req.user, user)
		NoContent
	}
}

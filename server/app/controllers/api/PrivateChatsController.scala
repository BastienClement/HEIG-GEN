package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import util.DateTime

@Singleton
class PrivateChatsController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	// TODO: check that user is a contact

	/**
	  * Lists message between two users.
	  */
	def list(user: Int) = UserAction.async { req =>
		val query = PrivateMessages.filter { m =>
			(m.from === req.user && m.to === user) || (m.from === user && m.to === req.user)
		}.sortBy(m => m.date.desc)

		query.run.map { list =>
			Ok(JsArray(list.map(_.toJson)))
		}
	}

	/**
	  * Sends a message to the user.
	  */
	def post(user: Int) = UserAction.async(parse.json) { req =>
		val text = (req.body \ "text").as[String]
		val query = PrivateMessages insert PrivateMessage(0, req.user, user, DateTime.now, text)
		query.run.map(m => Created(Json.obj("id" -> m.id)))
	}
}

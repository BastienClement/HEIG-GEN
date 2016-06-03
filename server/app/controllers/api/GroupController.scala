package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, JsBoolean}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

@Singleton
class GroupController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {
	/**
	  * Lists groups accessible by the user.
	  */
	def list = UserAction.async { req =>
		Groups.accessibleBy(req.user).map { group =>
			(group, UnreadFlags.groupUnread(req.user, group.id))
		}.run.map { case res =>
			Ok(JsArray(res.map {
				case (group, unread) => group.toJson + ("unread" -> JsBoolean(unread))
			}))
		}
	}

	def create = UserAction.async(parse.json) { req =>
		???
	}

	def info(id: Int) = UserAction.async { req =>
		???
	}

	def delete(id: Int) = NotYetImplemented
	def messages(id: Int) = NotYetImplemented
	def post(id: Int) = NotYetImplemented
	def members(id: Int) = NotYetImplemented
	def invite(id: Int, user: Int) = NotYetImplemented
	def kick(id: Int, user: Int) = NotYetImplemented
}

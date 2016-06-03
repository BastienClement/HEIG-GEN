package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, JsBoolean, JsObject, Json}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import scala.util.Success
import services.PushService
import util.DateTime

@Singleton
class GroupController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration, val push: PushService)
		extends Controller with ApiActionBuilder {
	/**
	  * Lists groups with a filter
	  */
	def groupsWithReadFlag(user: Int) = {
		Groups.accessibleBy(user).map { group =>
			(group, UnreadFlags.groupUnread(user, group.id))
		}
	}

	/**
	  * Serializes groups to JSON.
	  */
	def serializeGroup(data: (Group, Boolean)): JsObject = data match {
		case (group, unread) => group.toJson + ("unread" -> JsBoolean(unread))
	}

	/**
	  * Lists groups accessible by the user.
	  */
	def list = UserAction.async { req =>
		groupsWithReadFlag(req.user).run.map { users =>
			Ok(JsArray(users.map(serializeGroup)))
		}
	}

	/**
	  * Creates a new group and invite the user as an admin in the group.
	  */
	def create = UserAction.async(parse.json) { req =>
		val title = (req.body \ "title").as[String]
		Groups.insert(Group(0, title, DateTime.now)).run.andThen {
			case Success(group) => Members.invite(req.user, group.id, admin = true)
		}.map { group =>
			Created(Json.obj("id" -> group.id))
		}
	}

	/**
	  * Returns information about a specific group.
	  */
	def info(id: Int) = UserAction.async { req =>
		groupsWithReadFlag(req.user).filter { case (group, unread) =>
			group.id === id
		}.headOption.map { group_opt =>
			group_opt.map(serializeGroup).map(Ok(_)).getOrElse(NotFound('GROUPS_INFO_NOT_FOUND))
		}
	}

	def delete(id: Int) = NotYetImplemented
	def messages(id: Int) = NotYetImplemented
	def post(id: Int) = NotYetImplemented
	def members(id: Int) = NotYetImplemented
	def invite(id: Int, user: Int) = NotYetImplemented
	def kick(id: Int, user: Int) = NotYetImplemented
}

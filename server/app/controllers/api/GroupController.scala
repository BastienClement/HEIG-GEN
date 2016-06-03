package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc.{Controller, Result}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import services.PushService
import _root_.util.DateTime
import _root_.util.Implicits.futureWrapper
import java.sql.SQLException

@Singleton
class GroupController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration, val push: PushService)
		extends Controller with ApiActionBuilder {
	/**
	  * Lists groups with a filter
	  */
	private def groupsWithReadFlag(user: Int) = {
		Groups.accessibleBy(user).map { group =>
			(group, UnreadFlags.groupUnread(user, group.id))
		}
	}

	/**
	  * Serializes groups to JSON.
	  */
	private def serializeGroup(data: (Group, Boolean)): JsObject = data match {
		case (group, unread) => group.toJson + ("unread" -> JsBoolean(unread))
	}

	/**
	  * Checks whether a given user is admin of the group or not.
	  */
	private def isGroupMember(user: Int, group: Int, admin: Boolean = false): Future[Boolean] = {
		Members.filter { m =>
			m.user === user && m.group === group && (m.admin === true || !admin)
		}.exists.result.run
	}

	/**
	  * Ensures that the user is the admin of the group before executing action
	  */
	private def ensureGroupMember(user: Int, group: Int, admin: Boolean = false, failure: Symbol = 'GROUPS_ACTION_FORBIDDEN)
			(action: => Future[Result]): Future[Result] = {
		isGroupMember(user, group, admin).flatMap {
			case true => action
			case false => Forbidden(failure)
		}
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
		ensureGroupMember(req.user, id) {
			groupsWithReadFlag(req.user).filter { case (group, unread) =>
				group.id === id
			}.headOption.map { group_opt =>
				group_opt.map(serializeGroup).map(Ok(_)).getOrElse(NotFound('GROUPS_INFO_NOT_FOUND))
			}
		}
	}

	def delete(id: Int) = NotYetImplemented
	def messages(id: Int) = NotYetImplemented
	def post(id: Int) = NotYetImplemented

	/**
	  * List groups members.
	  */
	def members(id: Int) = UserAction.async { req =>
		ensureGroupMember(req.user, id) {
			Members.forGroup(id).run.map { members =>
				Ok(JsArray(members.map { m =>
					Json.obj("user" -> m.user, "joined" -> m.date, "admin" -> m.admin)
				}))
			}
		}
	}

	/**
	  * Invites a specific user in a group.
	  */
	def invite(id: Int, user: Int) = UserAction.async { req =>
		ensureGroupMember(req.user, id, admin = true) {
			Members.invite(user, id).map { _ =>
				NoContent
			}.recover {
				case e: SQLException if e.getErrorCode == 1452 => NotFound('GROUPS_INVITE_NOT_FOUND)
				case e: SQLException if e.getErrorCode == 1062 => Conflict('GROUPS_INVITE_DUPLICATE)
			}
		}
	}

	/**
	  * Removes a user from a group.
	  */
	def kick(id: Int, user: Int) = UserAction.async { req =>
		ensureGroupMember(req.user, id, admin = true) {
			Members.kick(user, id).map {
				case updated if updated > 0 => NoContent
				case _ => UnprocessableEntity('GROUPS_KICK_UNPROCESSABLE)
			}
		}
	}
}

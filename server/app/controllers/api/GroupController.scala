package controllers.api

import com.google.inject.{Inject, Singleton}
import java.sql.SQLException
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, JsBoolean, JsObject, Json}
import play.api.mvc.{Controller, Result}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import services.PushService
import util.DateTime
import util.Implicits.futureWrapper

@Singleton
class GroupController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration, val push: PushService)
		extends Controller with ApiActionBuilder {
	/**
	  * Lists groups with a filter
	  */
	private def groupsWithReadFlag(user: Int) = {
		Groups.accessibleBy(user).sortBy { group =>
			group.last_message.desc
		}.map { group =>
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
	private def isGroupMember(user: Int, group: Int, admin: Boolean = false): Future[Option[Member]] = {
		Members.filter { m =>
			m.user === user && m.group === group && (m.admin === true || !admin)
		}.headOption
	}

	/**
	  * Ensures that the user is the admin of the group before executing action
	  */
	private def ensureGroupMember(user: Int, group: Int, admin: Boolean = false, failure: Symbol = 'GROUPS_ACTION_FORBIDDEN)
			(action: => Future[Result]): Future[Result] = {
		isGroupMember(user, group, admin).flatMap {
			case Some(option) => action
			case None => Forbidden(failure)
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

	/**
	  * Deletes the group.
	  */
	def delete(id: Int) = UserAction.async { req =>
		ensureGroupMember(req.user, id, admin = true) {
			Members.forGroup(id).run.flatMap { members =>
				Groups.filter(g => g.id === id).delete.run.map(_ => NoContent).andThen {
					case Success(_) => for (member <- members) push.send(member.user, 'GROUP_DELETED, "group" -> id)
				}
			}
		}
	}

	/**
	  * Sets groups as read.
	  */
	def read(id: Int) = UserAction { req =>
		UnreadFlags.setGroupRead(req.user, id)
		NoContent
	}

	/**
	  * List of group messages.
	  */
	def messages(id: Int) = UserAction.async { req =>
		isGroupMember(req.user, id).flatMap {
			case Some(member) =>
				Messages.filter { m =>
					m.group === id && m.date >= member.date
				}.sortBy { m =>
					m.date.desc
				}.run.map { messages =>
					Ok(JsArray(messages.map(_.toJson)))
				}

			case None =>
				Forbidden('GROUPS_MESSAGES_FORBIDDEN)
		}
	}

	/**
	  * Posts a new message.
	  */
	def post(id: Int) = UserAction.async(parse.json) { req =>
		ensureGroupMember(req.user, id) {
			val text = (req.body \ "text").as[String]
			val now = DateTime.now
			(Messages += Message(0, id, req.user, now, text)).run.map(_ => NoContent).andThen {
				case Success(_) =>
					Groups.filter { g =>
						g.id === id && g.last_message < now
					}.map { g =>
						g.last_message
					}.update(now).run.andThen {
						case Success(_) =>
							push.broadcast(id, 'GROUP_MESSAGES_UPDATED, "group" -> id)
							UnreadFlags.setGroupUnread(req.user, id)
					}
			}
		}
	}

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
		ensureGroupMember(req.user, id, admin = req.user != user) {
			Members.kick(user, id).map {
				case updated if updated > 0 => NoContent
				case _ => UnprocessableEntity('GROUPS_KICK_UNPROCESSABLE)
			}
		}
	}

	/**
	  * Promotes a user to group admin.
	  */
	def promote(id: Int, user: Int) = UserAction.async { req =>
		ensureGroupMember(req.user, id, admin = true) {
			def member_admin_flag(member: Int) = {
				Members.filter { m =>
					m.user === member && m.group === id
				}.map { m =>
					m.admin
				}
			}

			member_admin_flag(user).update(true).run.map {
				case updated if updated > 0 =>
					member_admin_flag(req.user).update(false).run.andThen {
						case Success(_) =>
							push.broadcast(id, 'GROUP_ADMIN_CHANGED, "old" -> req.user, "new" -> user)
					}
					NoContent

				case _ => UnprocessableEntity('GROUPS_PROMOTE_UNPROCESSABLE)
			}
		}
	}
}

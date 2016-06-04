package models

import models.mysql._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import services.PushService
import util.DateTime

case class Member(user: Int, group: Int, date: DateTime, admin: Boolean)

class Members(tag: Tag) extends Table[Member](tag, "members") {
	def user = column[Int]("user", O.PrimaryKey)
	def group = column[Int]("group", O.PrimaryKey)
	def date = column[DateTime]("date")
	def admin = column[Boolean]("admin")

	def * = (user, group, date, admin) <> (Member.tupled, Member.unapply)
}

object Members extends TableQuery(new Members(_)) {
	def forGroup(group: Int): Query[Members, Member, Seq] = {
		Members.filter(m => m.group === group)
	}

	def invite(user: Int, group: Int, admin: Boolean = false)(implicit push: PushService, ec: ExecutionContext): Future[Int] = {
		val member = Member(user, group, DateTime.now, admin)
		(Members += member).run.andThen {
			case Success(_) => push.broadcast(group, 'GROUP_USER_INVITED,
				"group" -> group,
				"user" -> user,
				"admin" -> admin
			)
		}
	}

	def kick(user: Int, group: Int)(implicit push: PushService, ec: ExecutionContext): Future[Int] = {
		Members.filter { m =>
			m.user === user && m.group === group && m.admin === false
		}.delete.run.andThen {
			case Success(count) if count > 0 =>
				push.broadcast(group, 'GROUP_USER_KICKED, "group" -> group, "user" -> user)
				push.send(user, 'GROUP_USER_KICKED, "group" -> group, "user" -> user)
		}
	}
}

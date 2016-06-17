package models

import models.mysql._
import scala.util.Success
import services.PushService
import scala.concurrent.{ExecutionContext, Future}

case class UnreadFlag(user: Int, contact: Option[Int], group: Option[Int])

class UnreadFlags(tag: Tag) extends Table[UnreadFlag](tag, "unread_flags") {
	def user = column[Int]("user")
	def contact = column[Option[Int]]("contact")
	def group = column[Option[Int]]("group")

	def * = (user, contact, group) <> (UnreadFlag.tupled, UnreadFlag.unapply)
}

object UnreadFlags extends TableQuery(new UnreadFlags(_)) {
	/** Checks if a given contact is unread */
	def contactUnread(user: Int, contact: Rep[Int]): Rep[Boolean] = {
		UnreadFlags.filter(uf => uf.user === user && uf.contact === contact).exists
	}

	/** Sets the unread flag to an user's contact */
	def setContactUnread(user: Int, contact: Int)(implicit push: PushService, ec: ExecutionContext): Future[Int] = {
		(UnreadFlags += UnreadFlag(user, Some(contact), None)).run.andThen {
			case Success(_) => push.send(user, 'PRIVATE_MESSAGES_UNREAD, "contact" -> contact)
		}
	}

	/** Clears the unread flag for an user's contact */
	def setContactRead(user: Int, contact: Int)(implicit push: PushService, ec: ExecutionContext): Future[Int] = {
		UnreadFlags.filter(uf => uf.user === user && uf.contact === contact).delete.run.andThen {
			case Success(deleted) if deleted > 0 => push.send(user, 'PRIVATE_MESSAGES_READ, "contact" -> contact)
		}
	}

	/** Checks if a given group is unread */
	def groupUnread(user: Int, group: Rep[Int]): Rep[Boolean] = {
		UnreadFlags.filter(uf => uf.user === user && uf.group === group).exists
	}

	/** Sets the unread flags to an user's group */
	def setGroupUnread(sender: Int, group: Int)(implicit push: PushService, ec: ExecutionContext): Future[Unit] = {
		Members.forGroup(group).filter(g => g.user =!= sender).map(g => g.user).run.map { users =>
			for (user <- users) {
				(UnreadFlags += UnreadFlag(user, None, Some(group))).run.andThen {
					case Success(_) => push.send(user, 'GROUP_UNREAD, "group" -> group)
				}
			}
		}
	}

	/** Clears the unread flag from an user's group */
	def setGroupRead(user: Int, group: Int)(implicit push: PushService, ec: ExecutionContext): Future[Int] = {
		UnreadFlags.filter(uf => uf.user === user && uf.contact === group).delete.run.andThen {
			case Success(deleted) if deleted > 0 => push.send(user, 'GROUP_READ, "group" -> group)
		}
	}
}

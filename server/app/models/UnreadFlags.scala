package models

import models.mysql._
import scala.util.Success
import services.PushService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UnreadFlag(user: Int, contact: Option[Int], group: Option[Int])

class UnreadFlags(tag: Tag) extends Table[UnreadFlag](tag, "unread_flags") {
	def user = column[Int]("user")
	def contact = column[Option[Int]]("contact")
	def group = column[Option[Int]]("group")

	def * = (user, contact, group) <> (UnreadFlag.tupled, UnreadFlag.unapply)
}

object UnreadFlags extends TableQuery(new UnreadFlags(_)) {
	def contactUnread(user: Int, contact: Rep[Int]): Rep[Boolean] = {
		UnreadFlags.filter(uf => uf.user === user && uf.contact === contact).exists
	}

	def setContactUnread(user: Int, contact: Int)(implicit push: PushService): Future[Int] = {
		(UnreadFlags += UnreadFlag(user, Some(contact), None)).run.andThen {
			case Success(_) => push.send(user, 'PRIVATE_MESSAGES_UNREAD, "contact" -> contact)
		}
	}

	def setContactRead(user: Int, contact: Int)(implicit push: PushService): Future[Int] = {
		UnreadFlags.filter(uf => uf.user === user && uf.contact === contact).delete.run.andThen {
			case Success(deleted) if deleted > 0 => push.send(user, 'PRIVATE_MESSAGES_READ, "contact" -> contact)
		}
	}
}

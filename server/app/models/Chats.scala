package models

import models.mysql._
import play.api.libs.json._
import _root_.util.DateTime
import scala.concurrent.Future

case class Chat(id: Int, title: Option[String], group: Boolean, last_message: DateTime) {
	def toJson: JsObject = Json.obj(
		"id" -> id,
		"title" -> title.map(JsString).getOrElse(JsNull).asInstanceOf[JsValue],
		"group" -> group,
		"last_message" -> last_message
	)
}

class Chats(tag: Tag) extends Table[Chat](tag, "conversations") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def title = column[Option[String]]("title")
	def group = column[Boolean]("group")
	def last_message = column[DateTime]("last_message")

	def * = (id, title, group, last_message) <> (Chat.tupled, Chat.unapply)
}

object Chats extends TableQuery(new Chats(_)) {
	def accessibleBy(user: Int) = Chats.filter(chat => Members.filter(m => m.chat === chat.id && m.user === user).exists)

	def forUser(user: Int) = {
		val query = for {
			chat <- accessibleBy(user)
			mem <- Members if mem.chat === chat.id && mem.hidden === false
			user <- Users if user.id === mem.user
		} yield (chat, mem, user)
		query.sortBy(_._1.last_message.desc)
	}

	def createUserChat(user: Int): Future[Int] = ???
}

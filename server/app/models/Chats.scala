package models

import models.mysql._
import play.api.libs.json._
import _root_.util.DateTime

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
}

package models

import models.mysql._
import util.DateTime

case class Message(id: Int, conversation: Int, user: Int, date: DateTime, body: String)

class Messages(tag: Tag) extends Table[Message](tag, "messages") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def chat = column[Int]("fk_conversation")
	def user = column[Int]("fk_user")
	def date = column[DateTime]("date")
	def body = column[String]("body")

	def * = (id, chat, user, date, body) <> (Message.tupled, Message.unapply)
}

object Messages extends TableQuery(new Messages(_)) {

}

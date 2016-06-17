package models

import models.mysql._
import play.api.libs.json.{JsObject, Json}
import util.DateTime

case class PrivateMessage(id: Int, from: Int, to: Int, date: DateTime, text: String) {
	def toJson: JsObject = Json.obj(
		"id" -> id,
		"from" -> from,
		"to" -> to,
		"date" -> date,
		"text" -> text
	)
}

class PrivateMessages(tag: Tag) extends Table[PrivateMessage](tag, "private_messages") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def from = column[Int]("from")
	def to = column[Int]("to")
	def date = column[DateTime]("date")
	def text = column[String]("text")

	def * = (id, from, to, date, text) <> (PrivateMessage.tupled, PrivateMessage.unapply)
}

object PrivateMessages extends TableQuery(new PrivateMessages(_)) {
	/** Inserts a new PrivateMessage in the database */
	def insert(msg: PrivateMessage) = {
		PrivateMessages returning PrivateMessages.map(_.id) into ((msg, id) => msg.copy(id = id)) += msg
	}

	/** Queries messages between two users */
	def between(a: Int, b: Int): Query[PrivateMessages, PrivateMessage, Seq] = {
		PrivateMessages.filter(m => (m.from === a && m.to === b) || (m.from === b && m.to === a))
	}
}

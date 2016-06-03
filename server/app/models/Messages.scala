package models

import models.mysql._
import play.api.libs.json.{JsObject, Json}
import util.DateTime

case class Message(id: Int, group: Int, user: Int, date: DateTime, text: String) {
	def toJson: JsObject = Json.obj(
		"id" -> id,
		"group" -> group,
		"user" -> user,
		"date" -> date,
		"text" -> text
	)
}

class Messages(tag: Tag) extends Table[Message](tag, "messages") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def group = column[Int]("group")
	def user = column[Int]("user")
	def date = column[DateTime]("date")
	def text = column[String]("text")

	def * = (id, group, user, date, text) <> (Message.tupled, Message.unapply)
}

object Messages extends TableQuery(new Messages(_)) {

}

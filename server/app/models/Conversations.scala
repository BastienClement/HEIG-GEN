package models

import models.mysql._
import util.DateTime

case class Conversation(id: Int, title: String, tpe: String, date: DateTime)

class Conversations(tag: Tag) extends Table[Conversation](tag, "messages") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def title = column[String]("title")
	def tpe = column[String]("type")
	def date = column[DateTime]("dateCreation")

	def * = (id, title, tpe, date) <> (Conversation.tupled, Conversation.unapply)
}

object Conversations extends TableQuery(new Conversations(_)) {

}

package models

import models.mysql._
import util.DateTime

case class Member(user: Int, conversation: Int, date: DateTime, hidden: Boolean, admin: Boolean)

class Members(tag: Tag) extends Table[Member](tag, "members") {
	def user = column[Int]("fk_user", O.PrimaryKey)
	def chat = column[Int]("fk_conversation", O.PrimaryKey)
	def date = column[DateTime]("date")
	def hidden = column[Boolean]("hidden")
	def admin = column[Boolean]("admin")

	def * = (user, chat, date, hidden, admin) <> (Member.tupled, Member.unapply)
}

object Members extends TableQuery(new Members(_)) {

}

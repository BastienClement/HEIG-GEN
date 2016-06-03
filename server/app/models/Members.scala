package models

import models.mysql._
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
}

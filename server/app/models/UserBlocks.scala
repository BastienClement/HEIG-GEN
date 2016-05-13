package models

import models.mysql._
import util.DateTime

case class UserBlock(user: Int, blocked: Int, date: DateTime)

class UserBlocks(tag: Tag) extends Table[UserBlock](tag, "messages") {
	def user = column[Int]("user")
	def blocked = column[Int]("blockedUser")
	def date = column[DateTime]("date")

	def * = (user, blocked, date) <> (UserBlock.tupled, UserBlock.unapply)
}

object UserBlocks extends TableQuery(new UserBlocks(_)) {

}

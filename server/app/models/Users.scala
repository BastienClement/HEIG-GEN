package models

import models.mysql._

case class User(id: Int, name: String, pass: String, admin: Boolean)

class Users(tag: Tag) extends Table[User](tag, "gt_users") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def username = column[String]("username")
	def password = column[String]("password")
	def admin = column[Boolean]("admin")

	def * = (id, username, password, admin) <> (User.tupled, User.unapply)
}

object Users extends TableQuery(new Users(_)) {

}

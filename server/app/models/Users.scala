package models

import models.mysql._
import play.api.libs.json.{JsObject, Json}
import util.Crypto

case class User(id: Int, name: String, pass: String, admin: Boolean) {
	def toJson: JsObject = Json.obj("id" -> id, "name" -> name, "admin" -> admin)
}

class Users(tag: Tag) extends Table[User](tag, "users") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("username")
	def pass = column[String]("password")
	def admin = column[Boolean]("admin")

	def * = (id, name, pass, admin) <> (User.tupled, User.unapply)
}

object Users extends TableQuery(new Users(_)) {
	def findByUsername(username: String) = {
		Users.filter(u => u.name === username.toLowerCase).result.head
	}

	def register(username: String, password: String, admin: Boolean = false) = {
		Users += User(0, username.toLowerCase, Crypto.hash(password), admin)
	}
}

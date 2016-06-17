package models

import _root_.util.DateTime
import models.mysql._
import play.api.libs.json._

case class Group(id: Int, title: String, last_message: DateTime) {
	def toJson: JsObject = Json.obj(
		"id" -> id,
		"title" -> JsString(title),
		"last_message" -> last_message
	)
}

class Groups(tag: Tag) extends Table[Group](tag, "groups") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def title = column[String]("title")
	def last_message = column[DateTime]("last_message")

	def * = (id, title, last_message) <> (Group.tupled, Group.unapply)
}

object Groups extends TableQuery(new Groups(_)) {
	/** Inserts a new group in the database */
	def insert(group: Group) = {
		Groups returning Groups.map(_.id) into ((g, id) => g.copy(id = id)) += group
	}

	/** Builds a query for groups accessible by the user */
	def accessibleBy(user: Int): Query[Groups, Group, Seq] = {
		Groups.filter(group => Members.filter(m => m.group === group.id && m.user === user).exists)
	}
}

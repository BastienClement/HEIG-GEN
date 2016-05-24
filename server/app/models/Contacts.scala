package models

import models.mysql._

case class Contact(owner: Int, user: Int)

class Contacts(tag: Tag) extends Table[Contact](tag, "contacts") {
	def owner = column[Int]("owner", O.PrimaryKey)
	def user = column[Int]("user", O.PrimaryKey)

	def * = (owner, user) <> (Contact.tupled, Contact.unapply)
}

object Contacts extends TableQuery(new Contacts(_)) {

}

package models

import models.mysql._
import scala.concurrent.Future

case class Contact(lower: Int, upper: Int)

class Contacts(tag: Tag) extends Table[Contact](tag, "contacts") {
	def a = column[Int]("a", O.PrimaryKey)
	def b = column[Int]("b", O.PrimaryKey)

	def * = (a, b) <> (Contact.tupled, Contact.unapply)
}

object Contacts extends TableQuery(new Contacts(_)) {
	/** Returns the list of contacts for a user */
	def ofUser(user: Int) = {
		Contacts.filter(c => c.a === user || c.b === user)
	}

	/** Binds two users together as contact */
	def bind(a: Int, b: Int): Future[Int] = {
		if (a > b) bind(b, a)
		else (Contacts += Contact(a, b)).run
	}

	/** Unbinds two users */
	def unbind(a: Int, b: Int): Future[Int] = {
		if (a > b) unbind(b, a)
		else Contacts.filter(c => c.a === a && c.b === b).delete.run
	}

	/** Get the contact row between two users */
	def get(a: Int, b: Int): Query[Contacts, Contact, Seq] = {
		if (a > b) get(b, a)
		else Contacts.filter(c => c.a === a && c.b === b)
	}

	/** Check whether two users are contacts or not */
	def bound(a: Int, b: Int): Future[Boolean] = {
		get(a, b).exists.result.run
	}
}

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
	def ofUser(user: Int): Query[Contacts, Contact, Seq] = {
		Contacts.filter(c => c.a === user || c.b === user)
	}

	/** Returns the list of contacts for a user */
	def ofUserById(user: Int): Query[Rep[Int], Int, Seq] = {
		Contacts.filter(c => c.a === user || c.b === user).map(c => Case.If(c.a === user).Then(c.b).Else(c.a))
	}

	/** Binds two users together as contact */
	def bind(a: Int, b: Int): Future[Int] = {
		if (a > b) bind(b, a)
		else (Contacts += Contact(a, b)).run
	}

	/** Unbinds two users */
	def unbind(a: Int, b: Int): Future[Int] = {
		if (a > b) {
			unbind(b, a)
		} else {
			UnreadFlags.setContactRead(a, b)
			UnreadFlags.setContactRead(b, a)
			PrivateMessages.between(a, b).delete.run
			Contacts.filter(c => c.a === a && c.b === b).delete.run
		}
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

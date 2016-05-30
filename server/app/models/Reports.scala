package models

import models.mysql._
import util.DateTime

case class Report(id: Int, user: Int, message: Option[Int], date: DateTime, reason: String)

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def user = column[Int]("fk_user")
	def message = column[Option[Int]]("fk_message")
	def date = column[DateTime]("date")
	def reason = column[String]("reason")

	def * = (id, user, message, date, reason) <> (Report.tupled, Report.unapply)
}

object Reports extends TableQuery(new Reports(_)) {

}

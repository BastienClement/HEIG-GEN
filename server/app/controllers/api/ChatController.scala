package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, JsNumber}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

@Singleton
class ChatController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def list = UserAction.async { req =>
		val query = for {
			chat <- Chats.accessibleBy(req.user)
			mem <- Members if mem.chat === chat.id && mem.hidden === false
			user <- Users if user.id === mem.user
		} yield (chat, mem, user)

		query.sortBy(_._1.last_message.desc).run.map { results =>
			val data = results.groupBy { case (c, m, u) => c }.mapValues(l => l.map { case (c, m, u) => u })
			val list = data.toSeq.map {
				case (c, u) if c.group => c.toJson + ("members" -> JsNumber(u.size))
				case (c, u) if !c.group => c.toJson + ("user" -> u.find(_.id != req.user).get.toJson)
			}
			Ok(JsArray(list))
		}
	}

	def create = NotYetImplemented
	def info(id: Int) = NotYetImplemented
	def delete(id: Int) = NotYetImplemented
	def messages(id: Int) = NotYetImplemented
	def post(id: Int) = NotYetImplemented
	def members(id: Int) = NotYetImplemented
	def invite(id: Int, user: Int) = NotYetImplemented
	def kick(id: Int, user: Int) = NotYetImplemented
}

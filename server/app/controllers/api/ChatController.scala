package controllers.api

import com.google.inject.{Inject, Singleton}
import models._
import models.mysql._
import play.api.Configuration
import play.api.libs.json.{JsArray, JsNumber, Json}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext
import util.Implicits.futureWrapper

@Singleton
class ChatController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	private def chatsToArray(user: Int): Seq[(Chat, Member, User)] => JsArray = results => {
		val data = results.groupBy { case (c, m, u) => c }.mapValues(l => l.map { case (c, m, u) => u })
		val list = data.toSeq.map {
			case (c, u) if c.group =>
				c.toJson + ("members" -> JsNumber(u.size))
			case (c, u) if !c.group =>
				val other_user = u.find(_.id != user).get
				c.copy(title = Some(other_user.name)).toJson + ("user" -> other_user.toJson)
		}
		JsArray(list)
	}

	def list = UserAction.async { req =>
		Chats.forUser(req.user).run.map(chatsToArray(req.user)).map(Ok(_))
	}

	def create = UserAction.async(parse.json) { req =>
		(req.body \ "type").as[String] match {
			case "user_chat" =>
				val user = (req.body \ "user").as[Int]
				Chats.createUserChat(user).map { id =>
					Ok(Json.obj("id" -> id))
				}

			case "group_chat" =>
				NotImplemented('CHATS_CREATE_GROUP_NYI)
		}
	}

	def info(id: Int) = UserAction.async { req =>
		Chats.forUser(req.user).filter(_._1.id === id).run.map(chatsToArray(req.user)).map { res =>
			Ok((res \ 0).get)
		}
	}

	def delete(id: Int) = NotYetImplemented
	def messages(id: Int) = NotYetImplemented
	def post(id: Int) = NotYetImplemented
	def members(id: Int) = NotYetImplemented
	def invite(id: Int, user: Int) = NotYetImplemented
	def kick(id: Int, user: Int) = NotYetImplemented
}

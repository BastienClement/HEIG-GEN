package services

import com.google.inject.Singleton
import play.api.libs.json.{JsArray, JsValue, Json}
import scala.collection.mutable
import scala.concurrent.{Future, Promise}

@Singleton
class PushService {
	private final val REPLAY_SIZE = 10

	private val open = mutable.Map.empty[Int, List[Promise[JsValue]]].withDefaultValue(List.empty)
	private val replay = mutable.Map.empty[Int, Vector[(Int, JsValue)]].withDefaultValue(Vector.empty)

	private var next_id = 1

	def register(user: Int, from_id: Option[Int]): Future[JsValue] = this.synchronized {
		val missed =
			from_id.map { from =>
				replay(user).collect { case (id, value) if id <= from => value }
			}.getOrElse(Vector.empty)

		if (missed.nonEmpty) {
			Future.successful(Json.obj(
				"events" -> JsArray(missed),
				"next" -> next_id
			))
		} else {
			val promise: Promise[JsValue] = Promise()
			open(user) = promise :: open(user)
			promise.future
		}
	}

	def send(user: Int, data: JsValue) = this.synchronized {
		val ru = replay(user) :+ (next_id, data)
		replay(user) = if (ru.size > REPLAY_SIZE) ru.drop(ru.size - REPLAY_SIZE) else ru
		next_id += 1

		open(user).foreach { p =>
			p.success(Json.obj(
				"events" -> JsArray(Seq(data)),
				"next" -> next_id
			))
		}

		open.remove(user)
	}
}

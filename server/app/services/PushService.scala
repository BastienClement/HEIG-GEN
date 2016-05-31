package services

import com.google.inject.Singleton
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import scala.collection.mutable
import scala.concurrent.{Future, Promise}

/**
  * Handle HTTP long-polling notifications
  */
@Singleton
class PushService {
	/** Maximum size of the replay vectors */
	private final val REPLAY_SIZE = 10

	/** Pending promises */
	private val open = mutable.Map.empty[Int, List[Promise[JsObject]]].withDefaultValue(List.empty)

	/** Replay buffers */
	private val replay = mutable.Map.empty[Int, Vector[(Int, JsValue)]].withDefaultValue(Vector.empty)

	/** The next message ID */
	private var next_id = 1

	/**
	  * Awaits for the next notification event.
	  *
	  * If the ?from parameter was given, this method will attempt to send
	  * any missed event immediately.
	  *
	  * @param user    the user awaiting notification
	  * @param from_id the last received notification id
	  * @return a future that will be completed when an event is available
	  */
	def register(user: Int, from_id: Option[Int]): Future[JsObject] = this.synchronized {
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
			val promise: Promise[JsObject] = Promise()
			open(user) = promise :: open(user)
			promise.future
		}
	}

	/**
	  * Sends an event to a user.
	  * @param user the receiver user
	  * @param data the event data
	  */
	def send(user: Int, data: JsValue): Unit = this.synchronized {
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

package services

import _root_.util.DateTime
import com.google.inject.Singleton
import models.{Members, _}
import models.mysql._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import scala.collection.mutable
import scala.concurrent.{Future, Promise}

/**
  * Handle HTTP long-polling notifications
  */
@Singleton
class PushService {
	/** Maximum size of the replay vectors */
	private final val REPLAY_SIZE = 20

	/** Pending promises */
	private val open = mutable.Map.empty[Int, List[Promise[JsObject]]].withDefaultValue(List.empty)

	/** Replay buffers */
	private val replay = mutable.Map.empty[Int, Vector[PushEvent]].withDefaultValue(Vector.empty)

	/** The next message ID */
	private var next_id = 1

	/** A push event */
	case class PushEvent(data: JsValue) {
		val id = next_id
		next_id += 1

		val time = DateTime.now
	}

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
				replay(user).collect { case event if event.id > from => event.data }
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
	  */
	def send(user: Int, tpe: Symbol, data: (String, JsValueWrapper)*): Unit = this.synchronized {
		val effective_data = Json.obj(data: _*) + ("type" -> JsString(tpe.name))

		val ru = replay(user) :+ PushEvent(effective_data)
		replay(user) = if (ru.size > REPLAY_SIZE) ru.drop(ru.size - REPLAY_SIZE) else ru

		open(user).foreach { p =>
			p.success(Json.obj(
				"events" -> JsArray(Seq(effective_data)),
				"next" -> next_id
			))
		}

		open.remove(user)
	}

	/**
	  * Type of a broadcast target filter
	  */
	type BroadcastFilter = Rep[Int] => Rep[Boolean]

	/**
	  * Broadcast an event to every members of a group.
	  */
	def broadcast(group: Int, tpe: Symbol, data: (String, JsValueWrapper)*): Unit = {
		broadcastFilter(group, user => true, tpe, data: _*)
	}

	/**
	  * Broadcast an event to every members of a group with a filter.
	  */
	def broadcastFilter(group: Int, filter: BroadcastFilter, tpe: Symbol, data: (String, JsValueWrapper)*): Unit = {
		for {
			users <- Members.forGroup(group).map(g => g.user).filter(filter).run
			user <- users
		} send(user, tpe, data: _*)
	}

	/**
	  * Returns the list of replay messages available
	  */
	def debug = replay.toVector
}

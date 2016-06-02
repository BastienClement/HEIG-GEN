package controllers.api

import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext
import scala.util.Try
import services.PushService

class DummyController @Inject()(push: PushService)(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def nyi0() = NotYetImplemented
	def nyi1(a: String) = NotYetImplemented
	def nyi2(a: String, b: String) = NotYetImplemented

	def undefined(path: String) = Action { req =>
		NotFound('UNDEFINED_ACTION)
	}

	def events = UserAction.async { req =>
		val from = req.getQueryString("from").flatMap(v => Try(Integer.parseInt(v)).toOption)
		push.register(req.user, from).map(Ok(_))
	}

	def eventsDebug = UserAction { req =>
		if (!req.admin) {
			Forbidden('EVENTS_DEBUG_FORBIDDEN)
		} else {
			Ok(JsObject(push.debug.map { case (user, buffer) =>
				user.toString -> JsArray(buffer.map { case event =>
					Json.obj("id" -> event.id, "time" -> event.time.toISOString, "data" -> event.data)
				})
			}))
		}
	}
}

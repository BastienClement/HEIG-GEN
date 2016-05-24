package controllers.api

import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext

class DummyController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def nyi0() = NotYetImplemented
	def nyi1(a: String) = NotYetImplemented
	def nyi2(a: String, b: String) = NotYetImplemented

	def undefined(path: String) = Action { req =>
		NotFound(Json.obj("err" -> "The requested action does not exist"))
	}
}

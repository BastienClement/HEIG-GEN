package controllers.api

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

@Singleton
class ContactsController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def list = NotYetImplemented

	def add(user: Int) = NotYetImplemented

	def delete(user: Int) = NotYetImplemented
}

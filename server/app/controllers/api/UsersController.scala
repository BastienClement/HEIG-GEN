package controllers.api

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext

@Singleton
class UsersController @Inject()(implicit val ec: ExecutionContext, val conf: Configuration)
		extends Controller with ApiActionBuilder {

	def list = NotYetImplemented

	def user(id: Int) = NotYetImplemented
	def self = NotYetImplemented

	def delete = NotYetImplemented
}

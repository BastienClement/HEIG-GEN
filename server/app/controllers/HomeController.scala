package controllers

import javax.inject._
import play.api.mvc._
import services.ServerMeta

@Singleton
class HomeController @Inject()(meta: ServerMeta) extends Controller {
	def index = Action {
		Ok(views.html.index(s"Deployed: ${meta.boot}"))
	}
}

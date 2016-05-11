import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIOAction, NoStream}
import slick.driver.JdbcProfile
import slick.lifted.Query

package object models {
	val pkg = this
	@Inject var dbc: DatabaseConfigProvider = null

	lazy val mysql = slick.driver.MySQLDriver.api
	lazy val DB = dbc.get[JdbcProfile].db

	implicit class QueryExecutor[A](val q: Query[_, A, Seq]) extends AnyVal {
		import mysql._
		@inline def run = DB.run(q.result)
		@inline def head = DB.run(q.result.head)
		@inline def headOption = DB.run(q.result.headOption)
	}

	implicit class DBIOActionExecutor[R](val q: DBIOAction[R, NoStream, Nothing]) extends AnyVal {
		@inline def run = DB.run(q)
	}
}

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.language.higherKinds
import slick.dbio.{DBIOAction, NoStream}
import slick.driver.JdbcProfile
import slick.lifted.Query

package object models {
	/** Reference to the package object for DI */
	val pkg = this
	@Inject var dbc: DatabaseConfigProvider = null

	lazy val mysql = slick.driver.MySQLDriver.api
	lazy val DB = dbc.get[JdbcProfile].db

	/**
	  * Adds execution methods to Queries.
	  */
	implicit class QueryExecutor[A](val q: Query[_, A, Seq]) extends AnyVal {
		import mysql._
		@inline def run = DB.run(q.result)
		@inline def head = DB.run(q.result.head)
		@inline def headOption = DB.run(q.result.headOption)
	}

	/**
	  * Adds execution methods to DBIOActions.
	  */
	implicit class DBIOActionExecutor[R](val q: DBIOAction[R, NoStream, Nothing]) extends AnyVal {
		@inline def run = DB.run(q)
	}

	/**
	  * Adds methods to the Query object to help transforming it with
	  * monadic-style pipeline.
	  */
	implicit class QueryTransformer[A, B, C[_], D](val query: Query[A, B, C]) extends AnyVal {
		def optMap(opt: Option[D])(fn: (Query[A, B, C], D) => Query[A, B, C]) = opt match {
			case Some(value) => fn(query, value)
			case None => query
		}
	}
}

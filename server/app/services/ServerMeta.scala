package services

import com.google.inject.Singleton
import util.DateTime

/**
  * Information about the server process.
  */
@Singleton
class ServerMeta {
	/** Time of server start */
	val boot = DateTime.now.toISOString
}

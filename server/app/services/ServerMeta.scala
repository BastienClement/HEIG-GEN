package services

import com.google.inject.Singleton
import util.DateTime

@Singleton
class ServerMeta {
	val boot = DateTime.now.toISOString
}

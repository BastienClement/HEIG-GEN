package util

import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64
import play.api.Configuration
import play.api.libs.json._
import scala.util.Try

object Crypto {
	def hash(plaintext: String) = BCrypt.hashpw(plaintext, BCrypt.gensalt())

	def check(plaintext: String, ref: String) = BCrypt.checkpw(plaintext, ref)

	private def normalize(value: JsValue): String = value match {
		case obj: JsObject =>
			"{" + obj.fields
					.filter(p => !p._1.startsWith("$$"))
					.map(p => p._1 + ":" + normalize(p._2))
					.sorted
					.mkString(",") + "}"
		case arr: JsArray =>
			"[" + arr.value.map(normalize).mkString(",") + "]"
		case other =>
			other.toString()
	}

	private def computeSignature(obj: JsValue)(implicit conf: Configuration): String = {
		val utf8 = Charset.forName("UTF-8")
		val hmac = Mac.getInstance("HmacSHA256")
		hmac.init(new SecretKeySpec(utf8.encode(conf.getString("token.key").get).array(), "HmacSHA256"))
		val data = hmac.doFinal(utf8.encode(normalize(obj)).array())
		data.map(b => Integer.toString((b & 0xff) + 0x100, 16).substring(1)).mkString
	}

	def sign(obj: JsObject)(implicit conf: Configuration): String = {
		val signature = JsString(computeSignature(obj))
		val signed = obj + ("$$sign" -> signature)
		Base64.encodeBase64String(signed.toString().getBytes("UTF-8"))
	}

	def check(token: String)(implicit conf: Configuration): Option[JsObject] = {
		val json = new String(Base64.decodeBase64(token), "UTF-8")
		val obj = Try { Json.parse(json).as[JsObject] }.map(Some(_)).getOrElse(None)
		obj.filter(o => (o \ "$$sign").asOpt[String].contains(computeSignature(o)))
	}
}

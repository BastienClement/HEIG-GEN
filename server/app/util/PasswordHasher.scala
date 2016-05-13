package util

object PasswordHasher {
	def hash(plaintext: String) = BCrypt.hashpw(plaintext, BCrypt.gensalt())
	def check(plaintext: String, ref: String) = BCrypt.checkpw(plaintext, ref)
}

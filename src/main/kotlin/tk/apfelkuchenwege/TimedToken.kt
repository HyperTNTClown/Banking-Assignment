package tk.apfelkuchenwege

import io.ktor.util.*
import io.ktor.util.date.*

class TimedToken (
	val id: String
) {
	private val timestamp = getTimeMillis()

	val token = generateNonce()
		.sha256().base64()
		.replace("+", "")
		.replace("/", "")
		.replace("=", "")

	private companion object val MAX_DELTA = 300.000

	public fun valid(): Boolean {
		return getTimeMillis() -timestamp < MAX_DELTA
	}

}

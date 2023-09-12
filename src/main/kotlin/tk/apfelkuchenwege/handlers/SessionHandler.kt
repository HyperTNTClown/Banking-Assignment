package tk.apfelkuchenwege.handlers

import io.ktor.server.sessions.*
import io.ktor.util.*
import io.ktor.util.date.*
import tk.apfelkuchenwege.base64
import tk.apfelkuchenwege.data.banking.BankAccount
import tk.apfelkuchenwege.sha256

class SessionHandler {

	/**
	 * <Some sort of Session-ID
	 * (Probably something connected to the account
	 * + unique session identifier. Hashed and trimmed,
	 * Session Token - randomly generated, every request>
	 */
	private val currentSessionTokens: HashMap<String, Session> = HashMap()

	public fun addSession(account: BankAccount) {
		val id = (account.log + generateSessionId()).sha256().base64()
		currentSessionTokens[id] = Session(id)
	}

	public fun validateSession(id: String, token: String) : Boolean {
		if (!currentSessionTokens.containsKey(id)) return false
		if (currentSessionTokens[id]?.token != token) return false
		if (!currentSessionTokens[id]!!.valid()) return false
		return true
	}


}

class Session (
	private val id: String
) {
	private val timestamp = getTimeMillis()

	val token = generateNonce().sha256().base64()

	private companion object val MAX_DELTA = 300.000

	public fun valid(): Boolean {
		return getTimeMillis()-timestamp < MAX_DELTA
	}

}

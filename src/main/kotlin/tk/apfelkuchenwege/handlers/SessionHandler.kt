package tk.apfelkuchenwege.handlers

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import io.ktor.util.date.*
import tk.apfelkuchenwege.TimedToken
import tk.apfelkuchenwege.base64
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.data.banking.BankAccount
import tk.apfelkuchenwege.sha256

class SessionHandler {

	/**
	 * <Some sort of Session-ID
	 * (Probably something connected to the account
	 * + unique session identifier. Hashed and trimmed,
	 * Session Token - randomly generated, every request>
	 */
	private val currentSessionTokens: HashMap<String, TimedToken> = HashMap()

	public fun addSession(account: Account) : String {
		val id = (account.email.encodeBase64().replace("=", "") + generateSessionId()).sha256().base64()
		currentSessionTokens[id] = TimedToken(id)
		return currentSessionTokens[id]!!.token
	}

	public fun validateSession(id: String, token: String) : Boolean {
		if (!currentSessionTokens.containsKey(id)) return false
		if (currentSessionTokens[id]?.token != token) return false
		if (!currentSessionTokens[id]!!.valid()) return false
		return true
	}

	fun renewSession(call: ApplicationCall) {
		val session = currentSessionTokens[call.parameters["session"]]
		if (session != null) {
			if (validateSession(session.id, session.token)) {
				currentSessionTokens[session.id]!!.renew()
			}
		}
	}


}

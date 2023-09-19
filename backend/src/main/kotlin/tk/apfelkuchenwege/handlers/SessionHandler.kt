package tk.apfelkuchenwege.handlers

import com.google.gson.JsonObject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import io.ktor.util.date.*
import tk.apfelkuchenwege.TimedToken
import tk.apfelkuchenwege.base64
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.data.banking.AccountManager
import tk.apfelkuchenwege.data.banking.BankAccount
import tk.apfelkuchenwege.respondJson
import tk.apfelkuchenwege.sha256

class SessionHandler(val accountManager: AccountManager) {

	/**
	 * <Some sort of Session-ID
	 * (Probably something connected to the account
	 * + unique session identifier. Hashed and trimmed,
	 * Session Token - randomly generated, every request>
	 */
	private val currentSessionTokens: HashMap<String, TimedToken> = HashMap()

	private val errorMessage: JsonObject = JsonObject().apply {
		addProperty("status", "error")
		addProperty("message", "there was an error renewing the session. Please log in again")
	}

	public fun addSession(account: Account): String {
		val id = (account.email)
		var token = TimedToken(id)
		currentSessionTokens[token.token] = token
		return currentSessionTokens[token.token]!!.token
	}

	public fun validateSession(token: String): Boolean {
		if (!currentSessionTokens.containsKey(token)) return false
		if (currentSessionTokens[token]?.token != token) return false
		if (!currentSessionTokens[token]!!.valid()) return false
		return true
	}

	public fun getAccountFromSession(token: String): Account? {
		if (!validateSession(token)) return null
		return currentSessionTokens[token]?.id?.let { this.accountManager.getAccount(it) }
	}

	suspend fun renewSession(call: ApplicationCall) {
		val session = currentSessionTokens[call.parameters["token"]]
		if (session == null || !validateSession(session.token)) {
			call.respondJson(errorMessage, HttpStatusCode.InternalServerError)
			return
		}
		var newSession = currentSessionTokens[session.token]!!.renew()
		currentSessionTokens[newSession.token] = newSession
		currentSessionTokens.remove(session.token)
		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("token", newSession.token)
		call.respondJson(response)
	}

	suspend fun sessionValid(call: ApplicationCall) {
		val session = currentSessionTokens[call.parameters["token"]]
		if (session == null || !validateSession(session.token)) {
			var response = JsonObject().apply {
				addProperty("status", "success")
				addProperty("valid", false)
			}
			call.respondJson(response)
			return
		}
		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("valid", true)
		call.respondJson(response)
	}
}

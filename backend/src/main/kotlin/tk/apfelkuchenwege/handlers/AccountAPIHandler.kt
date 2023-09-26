package tk.apfelkuchenwege.handlers

import io.ktor.server.application.*
import tk.apfelkuchenwege.base64
import tk.apfelkuchenwege.respondJson
import tk.apfelkuchenwege.sha256

class AccountAPIHandler(private val sessionHandler: SessionHandler) {
	suspend fun getAccount(call: ApplicationCall) {
		var token = call.parameters["token"]!!
		println(token)
		this.sessionHandler.getAccountFromSession(token)?.let { call.respondJson(it.toJson()) }
		println(this.sessionHandler.getAccountFromSession(token)?.getBankAccounts())

	}
}

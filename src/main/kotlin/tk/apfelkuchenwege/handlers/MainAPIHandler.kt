package tk.apfelkuchenwege.handlers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tk.apfelkuchenwege.data.banking.BankAccountManager
import tk.apfelkuchenwege.api
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.data.banking.AccountManager

class MainAPIHandler(
	private val bankAccountManager: BankAccountManager,
	private val accountManager: AccountManager
) {

	val email = EMailAuthHandler()

	val sessionHandler = SessionHandler()

	suspend fun login(call: ApplicationCall) {
		var data = call.receive<String>()
		var json = JsonParser.parseString(data).asJsonObject

		println(json)

		var account = accountManager.getAccount(json.get("email").asString)
		if (account == null) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Invalid email or password")
			call.respondText(response.toString(), status = HttpStatusCode.BadRequest)
			return
		}

		if (!account.isVerified) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Account not verified")
			call.respondText(response.toString(), status = HttpStatusCode.BadRequest)
			return
		}

		if (!account.login(json.get("password").asString)) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Invalid email or password")
			call.respondText(response.toString(), status = HttpStatusCode.BadRequest)
			return
		}

		var token = sessionHandler.addSession(account)

		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("message", "Logged in")
		response.addProperty("token", token)
		call.respondText(response.toString())
	}

	suspend fun register(call: ApplicationCall) {
		var data = call.receive<String>()
		var json = JsonParser.parseString(data).asJsonObject

		println(json)

		var account = Account(
			json.get("email").asString,
			json.get("password").asString,
			json.get("firstName").asString,
			json.get("lastName").asString
		)

		accountManager.addAccount(account)
		email.pendingAccounts[account.email] = account
		email.sendVerificationMail(account)
	}

}

fun Application.configureRouting() {
	routing {
		route("/api/v1") {
			route("auth") {
				post("login") {
					api.login(call)
				}
				post("register") {
					api.register(call)
				}
				get("renew") {
					api.sessionHandler.renewSession(call)
				}
			}
			get("verify") {
				api.email.handleVerification(call)
			}
		}
	}
}

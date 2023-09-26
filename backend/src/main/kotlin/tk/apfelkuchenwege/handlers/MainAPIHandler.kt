package tk.apfelkuchenwege.handlers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import tk.apfelkuchenwege.data.banking.BankAccountManager
import tk.apfelkuchenwege.base64
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.data.banking.AccountManager
import tk.apfelkuchenwege.respondJson
import tk.apfelkuchenwege.sha256

class MainAPIHandler(
	private val bankAccountManager: BankAccountManager,
	private val accountManager: AccountManager
) {

	val email = EMailAuthHandler()

	val sessionHandler = SessionHandler(accountManager)
	val accountAPIHandler = AccountAPIHandler(sessionHandler)
	val bankingAPIHandler = BankingAPIHandler()

	suspend fun login(call: ApplicationCall) {
		var data = call.receive<String>()
		var json = JsonParser.parseString(data).asJsonObject

		println(json)

		var account = accountManager.getAccount(json.get("email").asString)
		if (account == null) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Invalid email or password: null")
			call.respondJson(response, HttpStatusCode.BadRequest)
			return
		}

		if (!account.isVerified) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Account not verified")
			call.respondJson(response, HttpStatusCode.BadRequest)
			return
		}

		if (!account.login(json.get("password").asString.sha256().base64())) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Invalid email or password")
			call.respondJson(response, HttpStatusCode.BadRequest)
			return
		}

		var token = sessionHandler.addSession(account)

		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("message", "Logged in")
		response.addProperty("token", token)
		call.respondJson(response)
	}

	suspend fun register(call: ApplicationCall) {
		var data = call.receive<String>()
		var json = JsonParser.parseString(data).asJsonObject

		println(json)

		var account = null as Account?;

		transaction {
			account = Account.new {
				email = json.get("email").asString
				password = json.get("password").asString.sha256().base64()
				firstName = json.get("firstName").asString
				lastName = json.get("lastName").asString
				isVerified = false
			}
		}

		if (!accountManager.addAccount(account!!)) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Email already in use")
			call.respondJson(response, HttpStatusCode.BadRequest)
			return
		}
		email.pendingAccounts[account!!.email] = account!!
		email.sendVerificationMail(account!!)

		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("message", "Account created\n Verification email sent")
		call.respondJson(response)
	}

}

fun Application.configureRouting(api: MainAPIHandler) {
	routing {
		route("/api/v1") {
			route("auth") {
				post("login") {
					api.login(call)
				}
				post("register") {
					api.register(call)
				}
				post("reset") {
					api.email.handlePasswordReset(call)
				}
				get("renew") {
					api.sessionHandler.renewSession(call)
				}
				get("valid") {
					api.sessionHandler.sessionValid(call)
				}
			}
			get("verify") {
				api.email.handleVerification(call)
			}
			route("banking") {
				get("accounts") {
					api.accountAPIHandler.getAccount(call)
				}
				post("deposit") {
					api.bankingAPIHandler.deposit(call)
				}
				post("withdraw") {
					api.bankingAPIHandler.withdraw(call)
				}
				post("transaction") {
					api.bankingAPIHandler.transaction(call)
				}
			}
		}
	}
}

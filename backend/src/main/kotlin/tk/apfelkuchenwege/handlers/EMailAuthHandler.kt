package tk.apfelkuchenwege.handlers

import com.google.gson.JsonObject
import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.transactions.transaction
import tk.apfelkuchenwege.TimedToken
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.respondJson
import tk.apfelkuchenwege.sg
import kotlin.concurrent.fixedRateTimer

class EMailAuthHandler {
	private val senderAddress: Email = Email("no-reply@apflkchn.tk", "Apfelkuchenwege Bank")
	private val subject = "Verify your account"
	val pendingAccounts: HashMap<String, Account> = HashMap()
	private val tokenMap: HashMap<String, TimedToken> = HashMap()

	init {
		fixedRateTimer("tokenCleaner", true, 0, 300000) {
			tokenMap.forEach { (key, value) ->
				if (!value.valid()) {
					tokenMap.remove(key)
				}
			}
		}
	}

	suspend fun handleVerification(call: ApplicationCall) {
		var token = call.parameters["token"]
		if (!checkToken(call, token)) return
		var account = pendingAccounts[tokenMap[token]!!.id]!!

		pendingAccounts.remove(account.email)
		tokenMap.remove(token)

		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("message", "Account verified")
		transaction {
			account.verify()
		}

		call.respondJson(response)
		return
	}

	fun sendVerificationMail(account: Account) {
		var accountEmail = Email(account.email, "${account.firstName} ${account.lastName}")
		val token = TimedToken(account.email)
		tokenMap[token.token] = token
		var content = Content("text/plain", "Please verify your account by clicking this link: https://bank.apflkchn.tk/verify?token=${token.token}")
		tokenMap[token.token] = token
		sendMail(accountEmail, content)
	}

	fun resetPasswordMail(account: Account) {
		var accountEmail = Email(account.email, "${account.firstName} ${account.lastName}")
		val token = TimedToken(account.email)
		pendingAccounts[account.email] = account
		tokenMap[token.token] = token
		var content = Content("text/plain", "Please reset your password by clicking this link: https://bank.apflkchn.tk/reset?token=${token.token}")
		tokenMap[token.token] = token
		sendMail(accountEmail, content)
	}

	private fun sendMail(accountEmail: Email, content: Content ) {
		if (System.getenv("DEV") == "true") {
			println(content.value)
			return
		}
		var mail = Mail(senderAddress, subject, accountEmail, content)
		var request = Request()
		try {
			request.method = Method.POST
			request.endpoint = "mail/send"
			request.body = mail.build()
			var response = sg.api(request)
			println(response.statusCode)
			println(response.body)
			println(response.headers)
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
	}

	suspend fun handlePasswordReset(call: ApplicationCall) {
		var token = call.parameters["token"]
		if (!checkToken(call, token)) return
		var account = pendingAccounts[tokenMap[token]!!.id]!!

		pendingAccounts.remove(account.email)
		tokenMap.remove(token)

		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("message", "Password reset")
		account.resetPassword(call.receive<String>())
		call.respondJson(response)
		return
	}

	private suspend fun checkToken(call: ApplicationCall, token: String?) : Boolean {
		if (token == null) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "No token provided")

			call.respondJson(response, HttpStatusCode.BadRequest)
			return false
		}
		if (!tokenMap.containsKey(token)) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Invalid token")

			call.respondJson(response, HttpStatusCode.BadRequest)
			return false
		}
		if (!tokenMap[token]!!.valid()) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Token expired")

			call.respondJson(response, HttpStatusCode.BadRequest)
			tokenMap.remove(token)
			return false
		}
		return true
	}
}

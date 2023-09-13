package tk.apfelkuchenwege.handlers

import com.google.gson.JsonObject
import com.sendgrid.Request
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import tk.apfelkuchenwege.TimedToken
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.sg
import kotlin.concurrent.fixedRateTimer

class EMailAuthHandler {
	private val senderAddress: Email = Email("no-reply@apflkchn.tk", "Apfelkuchenwege Bank")
	private val subject = "Verify your account"
	val pendingAccounts: HashMap<String, Account> = HashMap()
	private val tokenMap: HashMap<String, TimedToken> = HashMap()

	init {
		fixedRateTimer("tokenCleaner", false, 0, 300000) {
			tokenMap.forEach { (key, value) ->
				if (!value.valid()) {
					tokenMap.remove(key)
				}
			}
		}
	}

	suspend fun handleVerification(call: ApplicationCall) {
		var token = call.parameters["token"]
		if (token == null) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "No token provided")

			call.respondText(response.toString(), status = HttpStatusCode.BadRequest)
			return
		}
		if (!tokenMap.containsKey(token)) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Invalid token")

			call.respondText(response.toString(), status = HttpStatusCode.BadRequest)
			return
		}
		if (!tokenMap[token]!!.valid()) {
			var response = JsonObject()
			response.addProperty("status", "error")
			response.addProperty("message", "Token expired")

			call.respondText(response.toString(), status = HttpStatusCode.BadRequest)
			tokenMap.remove(token)
			return
		}
		var account = pendingAccounts[tokenMap[token]!!.id]!!

		pendingAccounts.remove(account.email)
		tokenMap.remove(token)

		var response = JsonObject()
		response.addProperty("status", "success")
		response.addProperty("message", "Account verified")

		call.respondText(response.toString())
		return
	}

	fun sendVerificationMail(account: Account) {
		var accountEmail = Email(account.email, "${account.firstName} ${account.lastName}")
		val token = TimedToken(account.email)
		println("Please verify your account by clicking this link: https://bank.apflkchn.tk/verify?token=${token.token}")
		// No way I'm wasting the free mails on testing
		// TODO: Uncomment this when deploying
		/*
		var content = Content("text/plain", "Please verify your account by clicking this link: https://bank.apflkchn.tk/verify?token=${token.token}")
		tokenMap[token.token] = token
		var mail = Mail(senderAddress, subject, accountEmail, content)
		var request = Request()
		try {
			request.method = com.sendgrid.Method.POST
			request.endpoint = "mail/send"
			request.body = mail.build()
			var response = sg.api(request)
			println(response.statusCode)
			println(response.body)
			println(response.headers)
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
		*/
	}
}

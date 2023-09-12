package tk.apfelkuchenwege.handlers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tk.apfelkuchenwege.data.banking.BankAccountManager
import tk.apfelkuchenwege.api

class MainAPIHandler(
	private val bankAccountManager: BankAccountManager
) {

	suspend fun handle(call: ApplicationCall) {

	}

	suspend fun login(call: ApplicationCall) {
		var data = call.receive<String>()
		var json = JsonParser.parseString(data)
		println(json)
		var response = JsonObject()
		response.addProperty("status", "success")
		call.respondText(response.toString())
	}

	suspend fun register(call: ApplicationCall) {
		var data = call.receive<String>()
		var json = JsonParser.parseString(data)


	}

}

fun Application.configureRouting() {
	routing {
		route("/api/v1/") {
			post("login") {
				api.login(call)
			}
			route("*") {
				handle { api.handle(call) }
			}
		}
	}
}

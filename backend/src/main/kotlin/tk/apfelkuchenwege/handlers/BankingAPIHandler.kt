package tk.apfelkuchenwege.handlers

import com.google.gson.JsonParser
import io.ktor.server.application.*
import io.ktor.server.request.*
import tk.apfelkuchenwege.receiveJson

class BankingAPIHandler {
	suspend fun deposit(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asNumber
		val amount = json.get("amount").asDouble

	}

}

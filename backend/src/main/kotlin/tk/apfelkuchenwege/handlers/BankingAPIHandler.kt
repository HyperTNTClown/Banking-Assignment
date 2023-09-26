package tk.apfelkuchenwege.handlers

import com.google.gson.JsonParser
import io.ktor.server.application.*
import io.ktor.server.request.*
import tk.apfelkuchenwege.api
import tk.apfelkuchenwege.data.banking.BankAccount
import tk.apfelkuchenwege.data.banking.BankAccounts
import tk.apfelkuchenwege.receiveJson

class BankingAPIHandler {
	suspend fun deposit(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asInt
		val amount = json.get("amount").asDouble

		api.sessionHandler.getAccountFromSession(token.asString)?.let {
			it.getBankAccount(acctNumber)?.deposit(amount)
		}
	}

	suspend fun withdraw(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asInt
		val amount = json.get("amount").asDouble

		api.sessionHandler.getAccountFromSession(token.asString)?.let {
			it.getBankAccount(acctNumber)?.withdraw(amount)
		}
	}

	suspend fun transaction(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asInt
		val amount = json.get("amount").asDouble
		val to = json.get("to").asInt

		api.sessionHandler.getAccountFromSession(token.asString)?.let {
			var recipient: BankAccount? = null
			org.jetbrains.exposed.sql.transactions.transaction {
				recipient = BankAccount.find { BankAccounts.acctNum eq to }.firstOrNull()
			}
			if (recipient == null) {
				it.getBankAccount(acctNumber)?.withdraw(amount)
				return
			}
			it.getBankAccount(acctNumber)?.transferTo(amount, recipient!!)
		}
	}

}

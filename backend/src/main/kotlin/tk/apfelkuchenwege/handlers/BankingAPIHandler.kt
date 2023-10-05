package tk.apfelkuchenwege.handlers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.ktor.server.application.*
import org.jetbrains.exposed.dao.id.EntityID
import tk.apfelkuchenwege.api
import tk.apfelkuchenwege.data.banking.*
import tk.apfelkuchenwege.receiveJson
import tk.apfelkuchenwege.respondJson
import java.util.UUID
import org.jetbrains.exposed.sql.transactions.transaction as sqlTransaction

class BankingAPIHandler {

	companion object {
		fun getNullAccount(): Account {
			return sqlTransaction {
				Account.findById(UUID(0, 0)) ?: Account.new(UUID(0, 0)) {
					email = ""
					password = ""
					firstName = ""
					lastName = ""
					isVerified = false
				}
			}
		}
	}

	suspend fun deposit(call: ApplicationCall) {
		val json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asInt
		val amount = json.get("amount").asDouble

		val account = api.sessionHandler.getAccountFromSession(token.asString)
			?: JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty(
					"message",
					"couldn't find account associated with your token. You might have to log in again"
				)
				call.respondJson(it)
				return
			}

		if (!account.getBankAccount(acctNumber)?.deposit(amount)!!) {
			JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty("message", "Something went wrong whilst depositing to your account")
				call.respondJson(it)
				return
			}
		}

		JsonObject().let {
			it.addProperty("status", "success")
			it.addProperty("message", "Successfully deposited $ $amount")
			call.respondJson(it)
		}
	}

	suspend fun withdraw(call: ApplicationCall) {
		val json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asInt
		val amount = json.get("amount").asDouble

		val account = api.sessionHandler.getAccountFromSession(token.asString)
			?: JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty(
					"message",
					"couldn't find account associated with your token. You might have to log in again"
				)
				call.respondJson(it)
				return
			}

		if (!account.getBankAccount(acctNumber)?.withdraw(amount)!!) {
			JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty("message", "Something went wrong whilst withdrawing from your account")
				call.respondJson(it)
				return
			}
		}

		JsonObject().let {
			it.addProperty("status", "success")
			it.addProperty("message", "Successfully withdrew $ $amount")
			call.respondJson(it)
		}
	}

	suspend fun transaction(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val acctNumber = json.get("acct").asInt
		val amount = json.get("amount").asDouble
		val to = json.get("to").asInt

		val account = api.sessionHandler.getAccountFromSession(token.asString)
			?: JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty(
					"message",
					"couldn't find account associated with your token. You might have to log in again"
				)
				call.respondJson(it)
				return
			}

		var recipient: BankAccount? = sqlTransaction {
			return@sqlTransaction BankAccount.find { BankAccounts.acctNum eq to }.firstOrNull()
		}
		if (recipient == null) {
			sqlTransaction {
				recipient = ExternalBankAccount.new {
					acctNum = to
					this.account = getNullAccount()
				}
			}
			println(recipient?.toJson())
		}
		if (account.getBankAccount(acctNumber)?.transferTo(amount, recipient!!) == false) {
			JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty("message", "There was a problem handling your transaction")
				call.respondJson(it)
				return
			}
		}

		JsonObject().let {
			it.addProperty("status", "success")
			it.addProperty("message", "Successfully transferred $ $amount to $to")
			call.respondJson(it)
		}
	}

	suspend fun history(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val accountNumberArray = json.get("accts").asJsonArray

		val account = api.sessionHandler.getAccountFromSession(token.asString)
			?: JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty(
					"message",
					"couldn't find account associated with your token. You might have to log in again"
				)
				call.respondJson(it)
				return
			}

		if (accountNumberArray.isEmpty) {
			for (acct in account.getBankAccounts()) {
				accountNumberArray.add(acct.acctNum)
			}
		}

		var transactions = mutableListOf<Transfer>()

		for (acct in accountNumberArray) {
			account.getBankAccount(acct.asInt)?.let {
				it.getHistory().toCollection(transactions)
			}
		}

		var j = JsonArray()

		for (t in transactions) {
			j.add(t.toJSON())
		}

		JsonObject().let {
			it.addProperty("status", "success")
			it.addProperty("message", "Successfully retrieved history")
			it.add("history", j)
			call.respondJson(it)
		}
	}

	suspend fun changeName(call: ApplicationCall) {
		var json = call.receiveJson()!!

		val token = json.get("token")
		val name = json.get("name").asString
		val accountNumber = json.get("acct").asInt

		val account = api.sessionHandler.getAccountFromSession(token.asString)
			?: JsonObject().let {
				it.addProperty("status", "failure")
				it.addProperty(
					"message",
					"couldn't find account associated with your token. You might have to log in again"
				)
				call.respondJson(it)
				return
			}

		sqlTransaction {
			account.getBankAccount(accountNumber)?.let {
				it.name = name
			}
		}

		JsonObject().let {
			it.addProperty("status", "success")
			it.addProperty("message", "Successfully changed name of $accountNumber to $name")
			call.respondJson(it)
		}
	}

}

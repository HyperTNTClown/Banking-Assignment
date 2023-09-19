package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BankAccountLogger(val bankAccount: BankAccount) {
	private val log = ArrayList<Transfer>()

	fun log(transfer: Transfer) {
		log.add(transfer)
	}

	fun deposit(amount: Double) {
		log(Transfer(null, bankAccount, amount, System.currentTimeMillis()))
	}

	fun withdraw(amount: Double) {
		log(Transfer(bankAccount, null, amount, System.currentTimeMillis()))
	}

	fun transfer(amount: Double, to: BankAccount) {
		log(Transfer(bankAccount, to, amount, System.currentTimeMillis()))
	}

	fun receive(amount: Double, from: BankAccount) {
		log(Transfer(from, bankAccount, amount, System.currentTimeMillis()))
	}


	private fun genTimestamp(): String {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss"))
	}

	fun toJson() : JsonArray {
		var json = JsonArray()
		for (transfer in log) {
			json.add(transfer.toJSON())
		}
		return json
	}

	@Override
	override fun toString(): String {
		var str = ""
		for (transfer in log) {
			str += transfer.toString() + "\n"
		}
		return str
	}
}

package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BankAccountLogger(val bankAccount: BankAccount) {

	fun deposit(amount: Double) {
		transaction {
			Transfer.new {
				from = null
				to = bankAccount.id
				this.amount = amount
				timestamp = System.currentTimeMillis()
			}
		}
	}

	fun withdraw(amount: Double) {
		transaction {
			Transfer.new {
				from = bankAccount.id
				to = null
				this.amount = amount
				timestamp = System.currentTimeMillis()
			}
		}
	}

	fun transfer(amount: Double, to: BankAccount) {
		transaction {
			Transfer.new {
				from = bankAccount.id
				this.to = to.id
				this.amount = amount
				timestamp = System.currentTimeMillis()
			}
		}
	}

	fun receive(amount: Double, from: BankAccount) {
		transaction {
			if (Transfer.find {
				(Transfers.to eq bankAccount.id) and (Transfers.from eq from.id) and (Transfers.amount eq amount) and (Transfers.timestamp eq System.currentTimeMillis())
			}.firstOrNull() == null) return@transaction
			Transfer.new {
				this.from = from.id
				to = bankAccount.id
				this.amount = amount
				timestamp = System.currentTimeMillis()
			}
		}
	}


	private fun genTimestamp(): String {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss"))
	}

	fun toJson() : JsonArray {
		var json = JsonArray()
		transaction {
			Transfers.select {
				(Transfers.from eq bankAccount.id) or (Transfers.to eq bankAccount.id)
			}.forEach {
				json.add(Transfer.wrapRow(it).toJSON())
			}
		}
		return json
	}

	@Override
	override fun toString(): String {
		var str = ""
		transaction {
			Transfers.select {
				(Transfers.from eq bankAccount.id) or (Transfers.to eq bankAccount.id)
			}.forEach {
				str += Transfer.wrapRow(it).toString() + "\n"
			}
		}
		return str
	}

	fun getTransfers(): List<Transfer> {
		var list = ArrayList<Transfer>()
		transaction {
			Transfers.select {
				(Transfers.from eq bankAccount.id) or (Transfers.to eq bankAccount.id)
			}.forEach {
				list.add(Transfer.wrapRow(it))
			}
		}
		return list
	}
}

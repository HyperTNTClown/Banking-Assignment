package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonObject
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object Transfers : IntIdTable() {
	var from = reference("from", BankAccounts).nullable().default(null)
	var to = reference("to", BankAccounts).nullable().default(null)
	var amount = double("amount")
	var timestamp = long("timestamp")
}

class Transfer(
	id: EntityID<Int>
) : IntEntity(id) {

	companion object : IntEntityClass<Transfer>(Transfers)

	var from by Transfers.from
	var to by Transfers.to
	var amount by Transfers.amount
	var timestamp by Transfers.timestamp

	@Override
	override fun toString(): String {
		if (from == null) {
			return "Deposit of $amount at $timestamp"
		}
		if (to == null) {
			return "Withdrawal of $amount at $timestamp"
		}
		return "Transfer of $amount from ${getAccount(from)} to ${getAccount(to)} at $timestamp"
	}

	fun getAccount(id: EntityID<Int>?): BankAccount? {
		if (id == null) return null
		return transaction { BankAccount.findById(id) }
	}

	fun toJSON(): JsonObject {
		return JsonObject().apply {
			addProperty("from", getAccount(from)?.acctNum.toString())
			addProperty("to", getAccount(to)?.acctNum.toString())
			addProperty("amount", amount)
			addProperty("timestamp", timestamp)
		}
	}
}

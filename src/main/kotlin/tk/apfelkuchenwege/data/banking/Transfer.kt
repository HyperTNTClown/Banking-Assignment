package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonObject

data class Transfer(
	val from: BankAccount?,
	val to: BankAccount?,
	val amount: Double,
	val timestamp: Long
) {
	@Override
	override fun toString(): String {
		if (from == null) {
			return "Deposit of $amount at $timestamp"
		}
		if (to == null) {
			return "Withdrawal of $amount at $timestamp"
		}
		return "Transfer of $amount from $from to $to at $timestamp"
	}

	fun toJSON(): JsonObject {
		return JsonObject().apply {
			addProperty("from", from?.acctNum)
			addProperty("to", to?.acctNum)
			addProperty("amount", amount)
			addProperty("timestamp", timestamp)
		}
	}
}

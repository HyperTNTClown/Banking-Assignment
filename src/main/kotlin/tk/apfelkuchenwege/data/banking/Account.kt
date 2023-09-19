package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import tk.apfelkuchenwege.api

class Account(
	val email: String,
	private var password: String,
	var firstName: String,
	var lastName: String
) {

	private val bankAccounts = ArrayList<BankAccount>()

	var isVerified = false

	fun verify() {
		isVerified = true
	}

	fun login(password: String) : Boolean {
		return this.password == password
	}

	init {
		bankAccounts.add(BankAccount(this))
		if (System.getenv("DEV") == "true") {
			verify()
		}
	}

	fun getBankAccounts(): List<BankAccount> {
		return bankAccounts
	}

	fun getBankAccount(acctNum: Int): BankAccount? {
		for (acct in bankAccounts) {
			if (acct.acctNum == acctNum) {
				return acct
			}
		}
		return null
	}

	fun requestPasswordReset() {
		api.email.resetPasswordMail(this)
	}

	fun resetPassword(newPassword: String) {
		password = newPassword
	}

	fun addBankAccount(): BankAccount {
		var acct = BankAccount(this)
		bankAccounts.add(acct)
		return acct
	}

	fun toJson() : JsonObject {
		var jsonArray = JsonArray()
		for (acct in bankAccounts) {
			jsonArray.add(acct.toJson())
		}
		return JsonObject().apply {
			addProperty("email", email)
			addProperty("firstName", firstName)
			addProperty("lastName", lastName)
			add("bankAccounts", jsonArray)
		}
	}



}

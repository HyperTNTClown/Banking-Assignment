package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import tk.apfelkuchenwege.api
import java.util.*
import kotlin.collections.ArrayList

object Accounts : UUIDTable() {
	val email = varchar("email", 255).uniqueIndex()
	var password = varchar("password", 255)
	val firstName = varchar("firstName", 255)
	val lastName = varchar("lastName", 255)
	val verified = bool("verified").default(false)
}

class Account(
	id: EntityID<UUID>
) : UUIDEntity(id) {

	companion object : UUIDEntityClass<Account>(Accounts)

	var email by Accounts.email
	var password by Accounts.password
	var firstName by Accounts.firstName
	var lastName by Accounts.lastName

	var isVerified by Accounts.verified

	fun verify() {
		isVerified = true
	}

	fun login(password: String) : Boolean {
		return this.password == password
	}

	init {
		EntityHook.subscribe {
			if (it.changeType == EntityChangeType.Created) {
				if (System.getenv("DEV") == "true") {
					//verify()
				}
			}
		}

	}

	fun getBankAccounts(): List<BankAccount> {
		// get all BankAccounts by account reference
		var bankAccounts = ArrayList<BankAccount>()
		transaction {
			BankAccount.find { BankAccounts.account eq this@Account.id }.all {
				bankAccounts.add(it)
			}
		}
		if (bankAccounts.isEmpty()) {
			bankAccounts.add(addBankAccount())
		}
		return bankAccounts
	}

	fun getBankAccount(acctNum: Int): BankAccount? {
		return transaction {
			BankAccount.find { BankAccounts.acctNum eq acctNum }.firstOrNull()
		}
	}

	fun requestPasswordReset() {
		api.email.resetPasswordMail(this)
	}

	fun resetPassword(newPassword: String) {
		password = newPassword
	}

	fun addBankAccount(): BankAccount {
		var acct = null as BankAccount?
		transaction {
			acct = BankAccount.new {
				account = this@Account
			}
		}
		return acct!!
	}

	fun toJson() : JsonObject {
		var jsonArray = JsonArray()
		for (acct in getBankAccounts()) {
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

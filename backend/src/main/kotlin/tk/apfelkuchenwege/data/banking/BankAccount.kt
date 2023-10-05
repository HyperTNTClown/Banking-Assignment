package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonObject
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.pow

object BankAccounts : IntIdTable() {
	val acctNum = integer("acctNum").uniqueIndex().clientDefault<Int> { (Math.random()*10.0.pow(9.0).toInt()).toInt() }
	val name = varchar("name", 255).default("Account")
	val balance = double("balance").default(0.0)
	val account = reference("account", Accounts)
}

open class BankAccount(
	id: EntityID<Int>
) : IntEntity(id) {

	companion object : IntEntityClass<BankAccount>(BankAccounts) {
		private val strArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()
		private const val MAX_PASSWORD_LENGTH = 20

		public fun genAcctNum(length: Int): Int {
			var acctNum = 0
			for (i in 0 until length) {
				acctNum = (acctNum + (Math.random() * 10).toInt() * 10.0.pow(i.toDouble())).toInt()
			}
			return acctNum
		}
	}

	var acctNum by BankAccounts.acctNum
	var name by BankAccounts.name
	var balance by BankAccounts.balance
	var account by Account referencedOn BankAccounts.account

	val logger = BankAccountLogger(this)

    open fun deposit(amount: Double): Boolean {
        if (amount < 0) {
            return false
        }
		transaction {
			balance += amount
		}
        logger.deposit(amount)
        return true
    }

    open fun withdraw(amount: Double): Boolean {
        if (amount < 0 || amount > balance) {
            return false
        }
		transaction {
        	balance -= amount
		}
        logger.withdraw(amount)
        return true
    }

    open fun transferTo(amount: Double, recipient: BankAccount): Boolean {
		println(amount)
		println(balance)
        if (amount < 0 || amount > balance) {
            return false
        }
        if (!recipient.receiveFrom(amount, this)) {
            return false
        }
		transaction {
        	balance -= amount
		}
        logger.transfer(amount, recipient)
        return true
    }

    protected open fun receiveFrom(amount: Double, sender: BankAccount): Boolean {
        if (amount < 0) {
            return false
        }
		transaction {
			balance += amount
		}
        logger.receive(amount, sender)
        return true
    }

    fun display() {
        println("Acct Num: $acctNum")
		println("Acct Name: $name")
        println("Balance: $balance")
        println("Log: \n ${logger.toString()}")
    }


    fun resetAcctNum() {
        acctNum = genAcctNum(9)
    }

	open fun toJson(): JsonObject {
		return JsonObject().apply {
			addProperty("acctNum", acctNum)
			addProperty("type", "internal")
			addProperty("name", name)
			addProperty("balance", balance)
			add("log", logger.toJson())
		}
	}

	fun getHistory(): List<Transfer> {
		return transaction {
			return@transaction logger.getTransfers()
		}
	}
}

class ExternalBankAccount(
	id: EntityID<Int>
) : BankAccount(id) {

	companion object : IntEntityClass<ExternalBankAccount>(BankAccounts)

	override fun deposit(amount: Double): Boolean {
		return false
	}

	override fun withdraw(amount: Double): Boolean {
		return false
	}

	override fun transferTo(amount: Double, recipient: BankAccount): Boolean {
		return false
	}

	override fun receiveFrom(amount: Double, sender: BankAccount): Boolean {
		return true
	}
	override fun toString(): String {
		return "External Account $acctNum"
	}

	override fun toJson() : JsonObject {
		return JsonObject().apply {
			addProperty("acctNum", acctNum)
			addProperty("type", "external")
		}
	}
}

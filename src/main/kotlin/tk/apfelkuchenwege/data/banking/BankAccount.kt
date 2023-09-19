package tk.apfelkuchenwege.data.banking

import com.google.gson.JsonObject
import java.lang.Math.pow
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow

class BankAccount(
	var acctNum: Int,
	var name: String,
	private var balance: Double,
	val account: Account,
) {

	val logger = BankAccountLogger(this)

    @JvmOverloads
    constructor(account: Account) : this(0, "Bank Account", 0.0, account) {
        acctNum = genAcctNum(9)
    }

    private fun genAcctNum(length: Int): Int {
        var acctNum = 0
        for (i in 0 until length) {
			acctNum = (acctNum + (Math.random() * 10).toInt() * 10.0.pow(i.toDouble())).toInt()
        }
        return acctNum
    }

    fun deposit(amount: Double): Boolean {
        if (amount < 0) {
            return false
        }
        balance += amount
        logger.deposit(amount)
        return true
    }

    fun withdraw(amount: Double): Boolean {
        if (amount < 0 || amount > balance) {
            return false
        }
        balance -= amount
        logger.withdraw(amount)
        return true
    }

    fun transferTo(amount: Double, recipient: BankAccount): Boolean {
        if (amount < 0 || amount > balance) {
            return false
        }
        if (!recipient.receiveFrom(amount, this)) {
            return false
        }
        balance -= amount
        logger.transfer(amount, recipient)
        return true
    }

    private fun receiveFrom(amount: Double, sender: BankAccount): Boolean {
        if (amount < 0) {
            return false
        }
        balance += amount
        logger.receive(amount, sender)
        return true
    }

    fun display() {
        println("Acct Num: $acctNum")
		println("Acct Name: $name")
        println("Balance: $balance")
        println("Log: \n ${logger.toString()}")
    }

    private fun genPassword(length: Int): String {
        val pswdBuilder = StringBuilder()
        for (i in 0 until length) {
            pswdBuilder.append(strArray[(Math.random() * strArray.size).toInt()])
        }
        val pswd = pswdBuilder.toString()
        return pswd
    }

    fun resetAcctNum() {
        acctNum = genAcctNum(9)
    }

    companion object {
        private const val MAX_PASSWORD_LENGTH = 20
        var strArray = arrayOf(
            "a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z", "A",
            "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z", "1", "2",
            "3", "4", "5", "6", "7", "8", "9", "0"
        )
    }

	fun toJson(): JsonObject {
		return JsonObject().apply {
			addProperty("acctNum", acctNum)
			addProperty("name", name)
			addProperty("balance", balance)
			add("log", logger.toJson())
		}
	}
}

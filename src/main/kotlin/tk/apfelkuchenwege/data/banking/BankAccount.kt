package tk.apfelkuchenwege.data.banking

import java.lang.Math.pow
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow

class BankAccount(
	var acctNum: Int,
	private var balance: Double,
	private var fName: String?,
	private var lName: String?,
	pswd: String,
	log: String
) {
    private var pswd: String
    var log: String
        private set

    @JvmOverloads
    constructor(fName: String? = null, lName: String? = null) : this(0, 0.0, fName, lName, "", "") {
        acctNum = genAcctNum(9)
        pswd = hashPswd(genPassword(MAX_PASSWORD_LENGTH))
    }

    fun clearLog(pswd: String): Boolean {
        if (!checkPswd(pswd)) {
            return false
        }
        log = ""
        return true
    }

    private fun clearLog() {
        log = ""
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
            log += genTimestamp() + " Deposit Failed [$" + amount + "]\n"
            return false
        }
        balance += amount
        log += genTimestamp() + " Deposit Successful [$" + amount + "]\n"
        return true
    }

    fun withdraw(amount: Double): Boolean {
        if (amount < 0 || amount > balance) {
            log += genTimestamp() + " Withdrawal Failed [$" + amount + "]\n"
            return false
        }
        balance -= amount
        log += genTimestamp() + " Withdrawal Successful [$" + amount + "]\n"
        return true
    }

    fun transferTo(amount: Double, recipient: BankAccount): Boolean {
        if (amount < 0 || amount > balance) {
            log += genTimestamp() + " Transfer Failed [$" + amount + " to  account" + recipient.acctNum + "]\n"
            return false
        }
        if (!recipient.receiveFrom(amount, this)) {
            log += genTimestamp() + " Transfer Failed [$" + amount + " to  account" + recipient.acctNum + "]\n"
            return false
        }
        balance -= amount
        log += genTimestamp() + " Transfer [$" + amount + " to  account" + recipient.acctNum + "]\n"
        return true
    }

    fun receiveFrom(amount: Double, sender: BankAccount): Boolean {
        if (amount < 0) {
            return false
        }
        balance += amount
        log += genTimestamp() + " Transfer [$" + amount + " received from account " + sender.acctNum + "]\n"
        return true
    }

    fun checkPswd(pswd: String): Boolean {
        return this.pswd == hashPswd(pswd)
    }

    fun resetPswd(crrntPswd: String, nwPswd: String): Boolean {
        val hashedCrrntPswd = hashPswd(crrntPswd)
        if (hashedCrrntPswd != pswd || nwPswd.length > MAX_PASSWORD_LENGTH) {
            log += genTimestamp() + " Reset Password Failed!\n"
            return false
        }
        pswd = hashPswd(nwPswd)
        log += genTimestamp() + " Password Successfully Changed!\n"
        return true
    }

    fun display() {
        println("Acct Num: $acctNum")
        println("Balance: $balance")
        println("First: $fName")
        println("Last: $lName")
        println("Password (Hashed): $pswd")
        println("Log: \n $log")
    }

    init {
        this.pswd = hashPswd(pswd.trim { it <= ' ' })
        this.log = log
    }

    private fun genPassword(length: Int): String {
        val pswdBuilder = StringBuilder()
        for (i in 0 until length) {
            pswdBuilder.append(strArray[(Math.random() * strArray.size).toInt()])
        }
        val pswd = pswdBuilder.toString()
        log += """${genTimestamp()} Generated password: $pswd
 This is your only chance to see it!
""" // can't go completely without showing it
        return pswd
    }

    // Will probably be done on the frontend when finished so the password isn't leaving the user in plaintext
    // but ain't no way I'm storing any password in plaintext
    private fun hashPswd(pswd: String): String {
        // SHA-256
        val digest: MessageDigest?
        digest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
        if (digest == null) {
            throw RuntimeException("MessageDigest.getInstance(\"SHA-256\") returned null")
        }
        val hash = digest.digest(pswd.toByteArray())
        // Base64 encoding
        return Base64.getEncoder().encodeToString(hash)
    }

    private fun genTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss"))
    }

    fun resetAcctNum() {
        acctNum = genAcctNum(9)
    }

    companion object {
        private const val MAX_PASSWORD_LENGTH = 5
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
}

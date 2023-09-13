package tk.apfelkuchenwege.data.banking

class AccountManager {
	private val accounts: HashMap<String, Account> = HashMap()

	private fun isEmailUnique(email: String): Boolean {
		for (acct in accounts.values) {
			if (acct.email == email) {
				return false
			}
		}
		return true
	}

	fun addAccount(account: Account) {
		while (!isEmailUnique(account.email)) {
			//account.resetEmail()
		}
		accounts.put(account.email, account)
	}

	fun removeAccount(email: String) {
		accounts.remove(email)
	}

	fun getAccount(email: String): Account? {
		return if (accounts.containsKey(email)) {
			accounts[email]
		} else {
			null
		}
	}

}

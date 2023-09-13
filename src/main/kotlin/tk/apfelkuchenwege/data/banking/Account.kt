package tk.apfelkuchenwege.data.banking

class Account(
	val email: String,
	private val password: String,
	var firstName: String,
	var lastName: String
) {

	private val BankAccounts = ArrayList<BankAccount>()

	var isVerified = false

	fun verify() {
		isVerified = true
	}

	fun login(password: String) : Boolean {
		return this.password == password
	}



}

package tk.apfelkuchenwege.data.banking

class Account(
	private val email: String,
	private val password: String,
	private val firstName: String,
	private val lastName: String
) {
	private val BankAccounts = ArrayList<BankAccount>()


}

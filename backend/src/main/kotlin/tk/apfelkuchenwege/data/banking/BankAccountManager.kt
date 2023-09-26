package tk.apfelkuchenwege.data.banking

class BankAccountManager() {
    private var bankAccounts: ArrayList<BankAccount>

    init {
        bankAccounts = ArrayList()
    }

    private fun isAcctNumUnique(acctNum: Int): Boolean {
        for (acct in bankAccounts) {
            if (acct.acctNum == acctNum) {
                return false
            }
        }
        return true
    }

    fun addAccount(bankAccount: BankAccount) {
        while (!isAcctNumUnique(bankAccount.acctNum)) {
            bankAccount.resetAcctNum()
        }
        bankAccounts.add(bankAccount)
    }

    fun removeAccount(acctNum: Int) {
        bankAccounts.removeIf { e: BankAccount -> e.acctNum == acctNum }
    }

    fun getAccount(acctNum: Int): BankAccount? {
        return if (bankAccounts.stream().anyMatch { e: BankAccount -> e.acctNum == acctNum }) {
            bankAccounts.stream().filter { e: BankAccount -> e.acctNum == acctNum }.findFirst().get()
        } else {
            null
        }
    }

    fun depositIntoAll(amount: Double) {
        for (account in bankAccounts) {
            account.deposit(amount)
        }
    }

    fun withdrawFromAll(amount: Double) {
        for (account in bankAccounts) {
            account.withdraw(amount)
        }
    }

    fun clearAccts() {
        bankAccounts = ArrayList()
    }

    fun display() {
        for (account in bankAccounts) {
            account.display()
        }
    }
}

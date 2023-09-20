import BankAccountComponent, {BankAccount} from './bank-account'

interface BankAccountsProps {
	accounts: BankAccount[]
}

export default function BankAccounts(props: BankAccountsProps) {
	return (
		<>
			{props.accounts.map((obj, i) => {
				return <div key={i} id={"acct"+i}><BankAccountComponent bankAccount={obj}/></div>
			})}
		</>
	)
}

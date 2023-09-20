interface BankAccountProps {
	bankAccount: BankAccount
}

export interface BankAccount {
	acctNum: number,
	name: string,
	balance: number,
	log: any[]
}

export default function BankAccountComponent(props: BankAccountProps) {
	return (
		<>
			<h1>{props.bankAccount.acctNum} - {props.bankAccount.name}</h1>
			<h2>${props.bankAccount.balance+0.0}</h2>
		</>
	)
}

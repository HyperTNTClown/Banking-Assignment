import styles from '../css/bank-account.module.css'

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
		<div className={styles.accountCard}>
			<div>
				<h1>{props.bankAccount.name}</h1>
				<span>{props.bankAccount.acctNum}</span>
			</div>
			<h2>${props.bankAccount.balance+0.0}</h2>
		</div>
	)
}

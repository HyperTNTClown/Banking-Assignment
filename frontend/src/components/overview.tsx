import BankAccountComponent, {BankAccount} from './bank-account';
import {getAccount} from "../bank-api.ts";
import {useEffect, useState} from "react";
import {RingLoader} from "react-spinners";


export default function Overview() {
	const [accountData, setAccountData] = useState(null)
	useEffect(() => {
		getAccount().then(acc => {
			setAccountData(acc)
		})

		return () => {}
	}, [])
	if (!accountData) return (<div id={"loading"}><RingLoader/></div>)
	return (
		<>
			{accountData.bankAccounts.reverse().map((obj, i) => {
				return <div key={i} id={"acct"+i}><BankAccountComponent bankAccount={obj}/></div>
			})}
		</>
	)
}

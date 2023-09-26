import BankAccountComponent, {BankAccount} from './bank-account';
import {getAccount} from "../bank-api.ts";
import {useEffect, useState} from "react";
import {RingLoader} from "react-spinners";


export default function Overview() {
	const [accountData, setAccountData] = useState(null)

	useEffect(() => {
		getAccount().then(acc => {
			console.log(acc)
			setAccountData(acc)
		})

		return () => {
			console.log("unmounting")
		}
	}, [])
	if (!accountData) return (<div id={"loading"}><RingLoader/></div>)
	return (
		<>
			{accountData.bankAccounts.map((obj, i) => {
				return <div key={i} id={"acct"+i}><BankAccountComponent bankAccount={obj}/></div>
			})}
		</>
	)
}

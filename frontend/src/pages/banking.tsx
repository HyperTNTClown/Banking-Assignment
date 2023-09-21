import {useEffect, useState} from "react";
import {getAccount} from "../bank-api.ts";
import useAuth from "../auth.ts";
import { Link, useLinkClickHandler } from "react-router-dom";
import BankAccounts from "../components/bank-accounts";
import {RingLoader} from "react-spinners";

export default function Banking() {
    const [accountData, setAccountData] = useState(null)
    const [ready, setReady] = useState(false)
	const {logout} = useAuth()

	const handleClick = useLinkClickHandler('/')

    useEffect(() => {
        getAccount().then(acc => {
            console.log(acc)
            setAccountData(acc)
            setReady(true)
			//document.getElementById('logout').addEventListener('onclick', () => {logout()})
        })

        return () => {
            console.log("unmounting")
        }
    }, [])

    if (!ready) {
        return (
            <>
				<RingLoader></RingLoader>
            </>
        )
    }
    return (
        <>
			<h1>
			<Link to={'/'} id={"logout"} onClick={(e) => {
				e.preventDefault()
				logout()
				handleClick(e)

			}}>Logout</Link>
			Banking </h1>
			<div id={"bankAccounts"}>
			<BankAccounts accounts={
				// @ts-ignore
				accountData.bankAccounts}/>
			</div>
        </>
    )
}

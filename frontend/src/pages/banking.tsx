import {useEffect, useState} from "react";
import {getAccount} from "../bank-api.ts";

export default function Banking() {
    const [accountData, setAccountData] = useState(null)
    const [ready, setReady] = useState(false)
    useEffect(() => {
        getAccount().then(acc => {
            console.log(acc)
            setAccountData(acc)
            setReady(true)
        })

        return () => {
            console.log("unmounting")
        }
    }, [])

    if (!ready) {
        return (
            <>
                <h1> Banking </h1>
            </>
        )
    }
    return (
        <>
            <h1> Banking </h1>
            <h2> {
				// @ts-ignore
				accountData.bankAccounts[0].balance} </h2>
        </>
    )
}

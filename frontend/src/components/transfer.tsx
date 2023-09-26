import {useEffect, useState} from "react";
import {deposit, getAccount, transaction, withdraw} from "../bank-api.ts";
import {RingLoader} from "react-spinners";
import CreatableSelect from "react-select/creatable";
import Select from "react-select";
import {theme} from "../theme.ts";
import styles from '../css/transfer.module.css'
import useAuth from "../auth.ts";

export default function Transfer() {

	const [accountData, setAccountData] = useState(null)
	const [state, setState] = useState("Transfer")
	const [sender, setSender] = useState(null)
	const [recipient, setRecipient] = useState(null)

	useEffect(() => {
		getAccount().then(acc => {
			console.log(acc)
			setAccountData(acc)
		})

		return () => {
			console.log("unmounting")
		}
	}, [])

	const change = (e) => {
		setState(e.target.innerText)
	}

	const transfer = () => {
		// @ts-ignore
		let amount = document.getElementById("amount").value
		if (state === "Withdraw") return withdraw(amount, sender)
		if (state === "Deposit") return deposit(amount, recipient)
		transaction(amount, sender, recipient).then(res => {
			console.log(res)
		});
	}

	if (!accountData) return (<div id={"loading"}><RingLoader/></div>)
	return (
		<div className={styles.root}>
			<nav className={styles.modeSelectionButtons}>
				<button onClick={change}>Transfer</button>
				<button onClick={change}>Deposit</button>
				<button onClick={change}>Withdraw</button>
			</nav>
			{state === "Transfer" ? <>
				{/* @ts-ignore */}
			<Select theme={theme} options={accountData.bankAccounts.map((obj, i) => {
				return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
			})} onChange={(e) => {
				console.log(e)
				// @ts-ignore
				setSender(e.value)
			}} placeholder={"Sender"}/>
				{/* @ts-ignore */}
			<CreatableSelect theme={theme} options={accountData.bankAccounts.map((obj, i) => {
				return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
			})} onChange={(e) => {
				console.log(e)
				// @ts-ignore
				setRecipient(e.value)
			}}
			formatCreateLabel={(s) => `Different BankAccount (${s})`} placeholder={"Recipient"}/>
			<input id={"amount"} type={"number"} placeholder={"Amount"}/>
			<button onClick={transfer}>Transfer</button>
			</> : <></>}
			{state === "Deposit" ? <>
				{/* @ts-ignore */}
			<Select theme={theme} options={accountData.bankAccounts.map((obj, i) => {
				return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
			})} onChange={(e) => {
				console.log(e)
				// @ts-ignore
				setRecipient(e.value)
			}} placeholder={"Receiving Account"}/>
			<input id={"amount"} type={"number"} placeholder={"Amount"}/>
			<button onClick={transfer}>Deposit</button>
			</> : <></>}
			{state === "Withdraw" ? <>
				{/* @ts-ignore */}
			<Select theme={theme} options={accountData.bankAccounts.map((obj, i) => {
				return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
			})} onChange={(e) => {
				console.log(e)
				// @ts-ignore
				setSender(e.value)
			}} placeholder={"Sending Account"}/>
			<input id={"amount"} type={"number"} placeholder={"Amount"}/>
			<button onClick={transfer}>Withdraw</button>
			</> : <></>}
		</div>
	)
}

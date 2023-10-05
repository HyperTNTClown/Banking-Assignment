import {useEffect, useState} from "react";
import {deposit, getAccount, transaction, withdraw} from "../bank-api.ts";
import {RingLoader} from "react-spinners";
import CreatableSelect from "react-select/creatable";
import Select from "react-select";
import {theme} from "../theme.ts";
import styles from '../css/transfer.module.css'
import CurrencyInput, {CurrencyInputProps} from "react-currency-input-field";
import ResponsePopup from "./ResponsePopup.tsx";
import useAuth from "../auth.ts";

export default function Transfer() {

	const [accountData, setAccountData] = useState(null)
	const [state, setState] = useState("Transfer")
	const [sender, setSender] = useState(null)
	const [recipient, setRecipient] = useState(null)
	const [value, setValue] = useState('')
	const [rawValue, setRawValue] = useState('')
	const [status, setStatus] = useState()
	const [message, setMessage] = useState()
	const [open, setOpen] = useState(false)

	useEffect(() => {
		getAccount().then(acc => {
			console.log(acc)
			setAccountData(acc)
		})

		return () => {
		}
	}, [])

	const change = (e) => {
		setState(e.target.innerText)
	}

	const handleOnValueChange = (value: string | undefined): void => {
		setRawValue(value === undefined ? 'undefined' : value || ' ');
		setValue(value);
	};

	const transfer = () => {
		// @ts-ignore
		let amount = parseInt(value)
		console.log(value)
		if (state === "Withdraw") withdraw(amount, sender).then(res => {
			setStatus(res.status)
			setMessage(res.message)
			setOpen(true)
		})
		if (state === "Deposit") deposit(amount, recipient).then(res => {
			setStatus(res.status)
			setMessage(res.message)
			setOpen(true)
		})
		if (state === "Transfer") transaction(amount, sender, recipient).then(res => {
			setStatus(res.status)
			setMessage(res.message)
			setOpen(true)
		});
	}

	const close = () => {
		setValue('')
		setSender(null)
		setRecipient(null)
		let s = state
		setState("")
		setTimeout(() => {
			setState(s)
			setOpen(false)
		}, 1)
	}

	if (!accountData) return (<div id={"loading"}><RingLoader/></div>)
	return (
		<div className={styles.root}>
			<nav className={styles.modeSelectionButtons}>
				<button onClick={change}>Transfer</button>
				<button onClick={change}>Deposit</button>
				<button onClick={change}>Withdraw</button>
			</nav>
			<ResponsePopup status={status} message={message} open={open} close={close}/>
			<div className={styles.controls}>
				{state === "Transfer" ? <>
					{/* @ts-ignore */}
					<Select theme={theme} options={accountData.bankAccounts.reverse().map((obj, i) => {
						return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
					})} onChange={(e) => {
						console.log(e)
						// @ts-ignore
						setSender(e.value)
					}} placeholder={"Sender"}/>
					{/* @ts-ignore */}
					<CreatableSelect theme={theme} options={accountData.bankAccounts.reverse().map((obj, i) => {
						return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
					})} onChange={(e) => {
						console.log(e)
						// @ts-ignore
						setRecipient(e.value)
					}}
									 formatCreateLabel={(s) => `Different BankAccount (${s})`}
									 placeholder={"Recipient"}/>
					<CurrencyInput id={"amount"} placeholder={"Amount"} decimalsLimit={2} prefix={"$"} onValueChange={handleOnValueChange}/>
					<button onClick={transfer} id={"transfer"}>Transfer</button>
				</> : <></>}
				{state === "Deposit" ? <>
					{/* @ts-ignore */}
					<Select theme={theme} options={accountData.bankAccounts.reverse().map((obj, i) => {
						return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
					})} onChange={(e) => {
						console.log(e)
						// @ts-ignore
						setRecipient(e.value)
					}} placeholder={"Receiving Account"}/>
					<CurrencyInput id={"amount"} placeholder={"Amount"} decimalsLimit={2} prefix={"$"} onValueChange={handleOnValueChange}/>
					<button onClick={transfer} id={"transfer"}>Deposit</button>
				</> : <></>}
				{state === "Withdraw" ? <>
					{/* @ts-ignore */}
					<Select theme={theme} options={accountData.bankAccounts.reverse().map((obj, i) => {
						return {label: obj.name + " - " + obj.acctNum + " (" + obj.balance + "$)", value: obj.acctNum}
					})} onChange={(e) => {
						console.log(e)
						// @ts-ignore
						setSender(e.value)
					}} placeholder={"Sending Account"}/>
					<CurrencyInput id={"amount"} placeholder={"Amount"} decimalsLimit={2} prefix={"$"} onValueChange={handleOnValueChange}/>
					<button onClick={transfer} id={"transfer"}>Withdraw</button>
				</> : <></>}
			</div>
		</div>
	)
}

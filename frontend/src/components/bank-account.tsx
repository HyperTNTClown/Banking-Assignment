import styles from '../css/bank-account.module.css'
import {changeAccountName} from "../bank-api.ts";
import ResponsePopup from "./ResponsePopup.tsx";
import {useRef, useState} from "react";

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
	const [status, setStatus] = useState()
	const [message, setMessage] = useState()
	const [open, setOpen] = useState(false)
	const icon = useRef(null);

	const enableSaveButton = () => {
		icon.current.style.display = "block"
	};
	const handleInputChange = (event: InputEvent) => {
		// TODO: Add Save Button, to dispatch the request (Maybe a PATCH?), backend needs to get this added too, but shouldn't be too complicated to pull of
		// https://www.w3.org/TR/DOM-Level-3-Events/#inputevent

		// @ts-ignore
		props.bankAccount.name = event.target.innerText

		enableSaveButton()
	}

	const save = () => {
		console.log(props.bankAccount.name)
		icon.current.style.display = "none"
		changeAccountName(props.bankAccount.name, props.bankAccount.acctNum).then(r => {
			setStatus(r.status)
			setMessage(r.message)
			setOpen(true)
		})
	}

	const close = () => {
		setOpen(false)
	}

	return (
		<>
			<ResponsePopup status={status} message={message} open={open} close={close}/>
			<div className={styles.accountCard}>
				<div>
					<svg ref={icon} id={styles.save} width="24" height="24" viewBox="0 0 24 24" fill="none"
						 xmlns="http://www.w3.org/2000/svg" style={{display: "none"}} onClick={save}>
						<path fillRule="evenodd" clipRule="evenodd"
							  d="M18.1716 1C18.702 1 19.2107 1.21071 19.5858 1.58579L22.4142 4.41421C22.7893 4.78929 23 5.29799 23 5.82843V20C23 21.6569 21.6569 23 20 23H4C2.34315 23 1 21.6569 1 20V4C1 2.34315 2.34315 1 4 1H18.1716ZM4 3C3.44772 3 3 3.44772 3 4V20C3 20.5523 3.44772 21 4 21L5 21L5 15C5 13.3431 6.34315 12 8 12L16 12C17.6569 12 19 13.3431 19 15V21H20C20.5523 21 21 20.5523 21 20V6.82843C21 6.29799 20.7893 5.78929 20.4142 5.41421L18.5858 3.58579C18.2107 3.21071 17.702 3 17.1716 3H17V5C17 6.65685 15.6569 8 14 8H10C8.34315 8 7 6.65685 7 5V3H4ZM17 21V15C17 14.4477 16.5523 14 16 14L8 14C7.44772 14 7 14.4477 7 15L7 21L17 21ZM9 3H15V5C15 5.55228 14.5523 6 14 6H10C9.44772 6 9 5.55228 9 5V3Z"
						/>
					</svg>
					<h1 //@ts-ignore
						onInput={handleInputChange} contentEditable={true}
						suppressContentEditableWarning={true}>{props.bankAccount.name}</h1>
					<span>{props.bankAccount.acctNum}</span>
				</div>
				<h2>${props.bankAccount.balance + 0.0}</h2>
			</div>
		</>
	)
}

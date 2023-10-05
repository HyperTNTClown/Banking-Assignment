import {sendGetRequest, sendPostRequest} from "./api.ts";

export async function getAccount() {
	let res = await sendGetRequest('/api/v1/banking/accounts')

	console.log(res)
	return res;
}

export async function transaction(amount: number, from: number, to: number) {
	let res = await sendPostRequest('/api/v1/banking/transaction', {
		amount: amount,
		acct: from,
		to: to
	})

	console.log(res)
	return res;
}

export async function deposit(amount: number, to: number) {
	let res = await sendPostRequest('/api/v1/banking/deposit', {
		amount: amount,
		acct: to
	})

	console.log(res)
	return res;
}

export async function withdraw(amount: number, from: number) {
	let res = await sendPostRequest('/api/v1/banking/withdraw', {
		amount: amount,
		acct: from
	})

	console.log(res)
	return res;
}

export async function getHistory(accounts: number[]) {
	let res = await sendPostRequest('/api/v1/banking/history', {
		accts: accounts
	})

	console.log(res)
	return res;
}

export async function changeAccountName(name: String, acct: number) {
	let res = await sendPostRequest('/api/v1/banking/change-name', {
		name: name,
		acct: acct
	})

	console.log(res)
	return res;
}

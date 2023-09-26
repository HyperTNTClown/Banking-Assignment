export async function getAccount() {
    let token = sessionStorage.getItem('token');
    let res = await fetch(`/api/v1/banking/accounts?token=${token}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })

    let text = await res.text();
    let json = JSON.parse(text);
    console.log(json)
    return json;
}

export async function transaction(amount: number, from: number, to: number) {
	let token = sessionStorage.getItem('token');
	let res = await fetch(`/api/v1/banking/transaction`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			token: token,
			amount: amount,
			acct: from,
			to: to
		})
	})

	let text = await res.text();
	let json = JSON.parse(text);
	console.log(json)
	return json;
}

export async function deposit(amount: number, to: number) {
	let token = sessionStorage.getItem('token');
	let res = await fetch(`/api/v1/banking/deposit`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			token: token,
			amount: amount,
			acct: to
		})
	})

	let text = await res.text();
	let json = JSON.parse(text);
	console.log(json)
	return json;
}

export async function withdraw(amount: number, from: number) {
	let token = sessionStorage.getItem('token');
	let res = await fetch(`/api/v1/banking/withdraw`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			token: token,
			amount: amount,
			acct: from
		})
	})

	let text = await res.text();
	let json = JSON.parse(text);
	console.log(json)
	return json;
}

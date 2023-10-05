export const sendPostRequest = async (url: string, data: any) => {
	data.token = sessionStorage.getItem('token');
	const response = await fetch(url, {
		method: 'POST',
		body: JSON.stringify(data),
		headers: {
			'Content-Type': 'application/json'
		}
	});
	let text = await response.text();
	return JSON.parse(text);
}

export const sendGetRequest = async (url: string) => {
	const response = await fetch(url+'?token='+sessionStorage.getItem('token'), {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	});
	let text = await response.text();
	return JSON.parse(text);
}

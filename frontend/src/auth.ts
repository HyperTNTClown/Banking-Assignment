import {useEffect, useState} from "react";
import {sendGetRequest, sendPostRequest} from "./api.ts";

export default function useAuth() {
	const getToken = () => {
		const token = sessionStorage.getItem('token');
		return token;
	}

	const [token, setToken] = useState(getToken());

	const saveToken = (userToken: string) => {
		sessionStorage.setItem('token', userToken);
		setToken(userToken);
	}

	const hashPassword = async (password: string) => {
		const hashedPassword = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(password))
		// @ts-ignore
		return btoa(String.fromCharCode.apply(null, new Uint8Array(hashedPassword)));
	}

	const login = async (email: string, password: string) => {
		let res = await sendPostRequest('/api/v1/auth/login', {email: email, password: await hashPassword(password)})

		console.log(res)
		if (res.status === 'success') {
			saveToken(res.token);
		}

		return res
	}

	const register = async (email: string, password: string, firstName: string, lastName: string) => {
		let res = await sendPostRequest('/api/v1/auth/register', {
			email: email,
			password: await hashPassword(password),
			firstName: firstName,
			lastName: lastName
		})

		return res;
	}

	// @ts-ignore
	const verify = async (token: string) => {
		sessionStorage.setItem('token', token);
		return await sendGetRequest(`/api/v1/verify`)
	}

	const valid = async () => {
		return (await sendGetRequest(`/api/v1/auth/valid`)).valid;
	}

	// @ts-ignore
	const loggedIn = async () => {
		if (token === undefined || token === '' || token === null) {
			return false;
		}
		return await valid();
	}

	// @ts-ignore
	const logout = () => {
		sessionStorage.removeItem('token');
		setToken('');
	}

	const renew = async () => {
		let json = await sendGetRequest(`/api/v1/auth/renew`)
		saveToken(json.token)
		return json;
	}

	useEffect(() => {
		loggedIn().then((valid) => {
			renew();
		})
	}, []);

	return {
		setToken: saveToken,
		token,
		login,
		register,
		verify,
		valid,
		loggedIn,
		logout
	}
}

import {useState} from "react";

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
        const base64Password = btoa(String.fromCharCode.apply(null, new Uint8Array(hashedPassword)));
        return base64Password;
    }

    const login = async (email: string, password: string) => {
        const res = await fetch('/api/v1/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({email: email, password: await hashPassword(password)})
        })

        const text = await res.text();
        const json = JSON.parse(text);
        console.log(json)
        if (json.status === 'success') {
            console.log('success')
            saveToken(json.token);
        }

		return {
			status: json.status
		}
    }

    const register = async (email: string, password: string, firstName: string, lastName: string) => {
        let res = await fetch('/api/v1/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({email: email, password: await hashPassword(password), firstName: firstName, lastName: lastName})
        })

        let text = await res.text();
        let json = JSON.parse(text);
        console.log(json)
		return json;
    }

	// @ts-ignore
    const verify = async (token: string) => {
        let res = await fetch(`/api/v1/verify?token=${token}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })

        let text = await res.text();
        let json = JSON.parse(text);
        console.log(json)
    }

    const valid = async () => {
        let res = await fetch(`/api/v1/auth/valid?token=${token}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })

        let text = await res.text();
        let json = JSON.parse(text);
        console.log(json)
        return json.valid;
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

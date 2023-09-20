import {Link, Navigate} from "react-router-dom";
import useAuth from "../auth.ts";
import {useState} from "react";

export default function Login() {
    const { login } = useAuth();
	const [success, setSuccess] = useState(false)
    return (
        <>
        <h1> Login
            <Link to={"/register"}>Register</Link>
        </h1>

            <form onSubmit={(e) => {
                e.preventDefault();
                const username = e.currentTarget.username.value;
                const password = e.currentTarget.password.value;
                login(username, password).then(r => {
					if (r.status == "success") {
						setSuccess(true)
					}
				});
            }}>
                <label htmlFor="username">Username</label>
                <input type="text" id="username" name="username" />
                <label htmlFor="password">Password</label>
                <input type="password" id="password" name="password" />
				{success ? <Navigate to={"/banking"} /> : <button type="submit">Login</button>}
            </form>
        </>
    )
}

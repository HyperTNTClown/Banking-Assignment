import {Link} from "react-router-dom";
import useAuth from "../auth.ts";

export default function Login() {
    const { login } = useAuth();
    return (
        <>
        <h1> Login
            <Link to={"/register"}>Register</Link>
        </h1>

            <form onSubmit={(e) => {
                e.preventDefault();
                const username = e.currentTarget.username.value;
                const password = e.currentTarget.password.value;
                login(username, password);
            }}>
                <label htmlFor="username">Username</label>
                <input type="text" id="username" name="username" />
                <label htmlFor="password">Password</label>
                <input type="password" id="password" name="password" />
                <button type="submit">Login</button>
            </form>
        </>
    )
}
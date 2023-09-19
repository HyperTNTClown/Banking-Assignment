import useAuth from "../auth.ts";
import {Link} from "react-router-dom";

export default function Register() {

    const { register } = useAuth();

    return (
        <>
            <h1>
                <Link to={"/login"}>Login</Link>
                Register </h1>
            <form onSubmit={(e) => {
                e.preventDefault();
                const firstName = e.currentTarget.firstName.value;
                const lastName = e.currentTarget.lastName.value;
                const email = e.currentTarget.email.value;
                const password = e.currentTarget.password.value;
                const password2 = e.currentTarget.password2.value;
                if (password === password2) {
                    register(email, password, firstName, lastName);
                }
            }}>
                <label htmlFor="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" />
                <label htmlFor="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" />
                <label htmlFor="email">Email</label>
                <input type="text" id="email" name="email" />
                <label htmlFor="password">Password</label>
                <input type="password" id="password" name="password" />
                <label htmlFor="password2">Confirm Password</label>
                <input type="password" id="password2" name="password2" />
                <button type="submit">Register</button>
            </form>
        </>
    )
}
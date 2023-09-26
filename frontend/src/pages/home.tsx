import {Link, Navigate} from "react-router-dom";

export default function Home() {
    return (
        <h1> Home
        <Link to={"/login"}>Log in</Link>
			<Navigate to={"/login"}/>
        </h1>
    )
}

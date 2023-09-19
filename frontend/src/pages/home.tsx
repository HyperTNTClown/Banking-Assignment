import {Link} from "react-router-dom";

export default function Home() {
    return (
        <h1> Home
        <Link to={"/login"}>Log in</Link>
        </h1>
    )
}
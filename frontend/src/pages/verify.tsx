
import useAuth from "../auth.ts";
import {Navigate} from "react-router-dom";

export default function Verify() {

	const { verify } = useAuth();

    const params = new URLSearchParams(window.location.search);
	const token = params.get('token');

    if (!token) return <Navigate to={"/login"} />;

	verify(token);

    return (
        <Navigate to={"/login"} />
    )
}

import useAuth from "../auth.ts";

export default function Verify() {

    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (!token) {
        return (
            <h1> Verify </h1>
        )
    }

    const { verify } = useAuth();
    verify(token);



    return (
        <h1> Verify </h1>
    )
}
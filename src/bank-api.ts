export async function getAccount() {
    let token = sessionStorage.getItem('token');
    let res = await fetch(`/api/v1/banking/accounts?token=${token}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })

    let text = await res.text();
    let json = JSON.parse(text);
    console.log(json)
    return json;
}
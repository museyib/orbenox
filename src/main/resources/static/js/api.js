async function apiRequest(url, method, body = null) {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    }
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    const options = {
        method: method,
        headers: headers};
    if (body) options.body = JSON.stringify(body);
    const response = await fetch(url, options);
    return response.json();
}

function moveOption(option, from, to, oppositeHandler) {
    from.removeChild(option);
    const newOption = new Option(option.innerText, option.value);
    newOption.ondblclick = () => oppositeHandler(newOption);
    to.appendChild(newOption);
}
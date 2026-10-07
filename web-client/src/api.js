export async function apiRequest(url, method, body = null, idempotencyKey = null) {
    const baseURL = 'http://localhost:8080';
    const token = localStorage.getItem('token');
    const locale = localStorage.getItem('locale') || navigator.language || 'en'
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Accept-Language': locale,
    }
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    if (idempotencyKey) {
        headers['Idempotency-Key'] = idempotencyKey;
    }
    const options = {
        method: method,
        headers: headers
    };
    if (body)
        options.body = JSON.stringify(body);
    const response = await fetch(baseURL + url, options);
    return response.json();
}

export function createIdempotencyKey() {
    if (globalThis.crypto?.randomUUID) {
        return globalThis.crypto.randomUUID();
    }
    return `${Date.now()}-${globalThis.crypto.getRandomValues(new Uint32Array(4)).join('-')}`;
}

export function refreshToken(success, failure) {
    localStorage.removeItem('token');
    const data = {
        username: localStorage.getItem('username'),
        password: localStorage.getItem('password'),
    }

    let refreshAttempts = localStorage.getItem('refreshAttempts');
    if (!refreshAttempts)
        refreshAttempts = 0;
    localStorage.setItem('refreshAttempts', refreshAttempts + 1);

    apiRequest('/api/auth/login', 'POST', data).then(response => {
        if (response.code === 200) {
            localStorage.setItem('token', response.data);
            localStorage.removeItem('refreshAttempts');
            success();
        } else {
            failure();
        }
        if (refreshAttempts > 5)
            failure();
    })
}

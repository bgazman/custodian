import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';

function OAuthCallback() {
    const location = useLocation();
    const [error, setError] = useState(null);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const code = params.get('code');
        const state = params.get('state');

        if (!code) {
            setError('No authorization code received');
            return;
        }

        const exchangeToken = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/oauth/token`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Basic ' + btoa(`${import.meta.env.VITE_CLIENT_ID}:${import.meta.env.VITE_CLIENT_SECRET}`)
                    },
                    body: JSON.stringify({
                        grantType: 'authorization_code',
                        code,
                        state,
                        redirectUri: import.meta.env.VITE_REDIRECT_URI
                    })
                });

                if (!response.ok) {
                    const error = await response.json();
                    throw new Error(error.message || 'Token exchange failed');
                }

                const data = await response.json();
                sessionStorage.setItem('access_token', data.accessToken);
                if (data.refreshToken) {
                    sessionStorage.setItem('refresh_token', data.refreshToken);
                }

                window.location.href = '/dashboard';
            } catch (err) {
                setError(err.message);
            }
        };

        exchangeToken();
    }, [location]);

    if (error) {
        return <div className="p-4 text-red-600">Authentication error: {error}</div>;
    }

    return <div className="p-4">Processing authentication...</div>;
}

export default OAuthCallback;
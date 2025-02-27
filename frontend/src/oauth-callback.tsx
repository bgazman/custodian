import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

function OAuthCallback() {
    const location = useLocation();
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const code = params.get('code');
        const returnedState = params.get('state');
        const storedState = localStorage.getItem('oauth_state');
        const codeVerifier = localStorage.getItem('code_verifier');

        if (!code) {
            setError('No authorization code received');
            setIsLoading(false);
            return;
        }

        if (returnedState !== storedState) {
            setError('State mismatch - possible CSRF attack');
            setIsLoading(false);
            return;
        }

        const exchangeToken = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/oauth/token`, {
                    method: 'POST',
                    credentials: 'include',
                    mode: 'cors',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Basic ' + btoa(`${import.meta.env.VITE_CLIENT_ID}:${import.meta.env.VITE_CLIENT_SECRET}`)
                    },
                    body: JSON.stringify({
                        grantType: 'authorization_code',
                        code,
                        codeVerifier,
                        state: returnedState
                    })
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.error_description || `HTTP error! status: ${response.status}`);
                }

                const data = await response.json();

                sessionStorage.setItem('access_token', data.access_token);
                sessionStorage.setItem('refresh_token', data.refresh_token);

                // Cleanup
                localStorage.removeItem('oauth_state');
                localStorage.removeItem('code_verifier');

                navigate('/dashboard', { replace: true });
            } catch (err: any) {
                console.error('Token exchange error:', err);
                setError(err.message || 'Failed to exchange authorization code for tokens');
            } finally {
                setIsLoading(false);
            }
        };

        exchangeToken();
    }, [location, navigate]);

    if (error) {
        return (
            <div className="p-4 bg-red-50 border border-red-200 rounded">
                <h2 className="text-lg font-semibold text-red-700 mb-2">Authentication Error</h2>
                <p className="text-red-600">{error}</p>
                <button
                    onClick={() => navigate('/')}
                    className="mt-4 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                >
                    Return to Home
                </button>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="p-4 text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mx-auto mb-4"></div>
                <p className="text-gray-600">Processing authentication...</p>
            </div>
        );
    }

    return null;
}

export default OAuthCallback;
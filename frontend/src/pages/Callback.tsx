import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthApiClient } from '../api/common/AuthApiClients';
import { useAuthentication } from '../context/AuthenticationContext';

const CallbackPage = () => {
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true); // Loading state for authentication
    const { login } = useAuthentication();

    useEffect(() => {
        const handleTokenExchange = async () => {
            // Check if the token exchange process has already been handled
            if (sessionStorage.getItem('token_exchange_processed')) {
                setLoading(false); // Prevent infinite loading
                navigate('/dashboard');
                return;
            }

            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
            const state = urlParams.get('state');
            const storedState = sessionStorage.getItem('oauth_state');

            if (state !== storedState) {
                setError('Invalid state parameter');
                setTimeout(() => navigate('/'), 3000);
                setLoading(false);
                return;
            }

            if (!code) {
                setError('Authorization code missing');
                setTimeout(() => navigate('/'), 3000);
                setLoading(false);
                return;
            }

            try {
                const payload = {
                    code,
                    redirectUri: import.meta.env.VITE_REDIRECT_URI,
                    clientId: import.meta.env.VITE_CLIENT_ID,
                    grantType: "authorization_code",
                };

                const response = await Promise.race([
                    AuthApiClient.post('/oauth/token', payload),
                    new Promise((_, reject) =>
                        setTimeout(() => reject(new Error('Request timeout')), 10000)
                    ),
                ]);

                if (!response?.accessToken) {
                    throw new Error('Invalid token response');
                }

                // Save tokens and user info directly to sessionStorage
                sessionStorage.setItem('access-token', response.accessToken);
                sessionStorage.setItem('refresh-token', response.refreshToken);
                sessionStorage.setItem('id-token', response.idToken);
                sessionStorage.setItem('user', JSON.stringify(response.userInfo || {}));

                // Call login with the stored tokens and user info
                login(
                    {
                        accessToken: response.accessToken,
                        refreshToken: response.refreshToken,
                        idToken: response.idToken,
                    },
                    response.userInfo || {}
                );

                // Mark the process as handled
                sessionStorage.setItem('token_exchange_processed', 'true');

                navigate('/dashboard'); // Navigate to the dashboard
            } catch (error) {
                const errorMessage = error.response?.data?.message || error.message;
                setError(`Authentication failed: ${errorMessage}`);
                setTimeout(() => navigate('/'), 3000);
            } finally {
                setLoading(false); // Ensure loading is stopped
            }
        };

        handleTokenExchange();
    }, [navigate, login]);

    return (
        <div className="flex items-center justify-center min-h-screen">
            {loading ? (
                <div className="flex flex-col items-center gap-4">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900" />
                    <p>Authenticating...</p>
                </div>
            ) : error ? (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                    <p>{error}</p>
                </div>
            ) : null}
        </div>
    );
};

export default CallbackPage;

import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthApiClient } from '../api/common/AuthApiClients';
import { useAuthentication } from '../components/Main/context/AuthenticationContext';

const CallbackPage = () => {
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const { login } = useAuthentication();
    const tokenExchangeProcessed = useRef(false);
    const tokenExchangePromise = useRef(null);

    useEffect(() => {
        const handleTokenExchange = async () => {
            if (tokenExchangeProcessed.current) {
                console.log('Token exchange already processed.');
                setLoading(false);
                navigate('/dashboard');
                return;
            }

            if (tokenExchangePromise.current) {
                await tokenExchangePromise.current;
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

            tokenExchangePromise.current = (async () => {
                try {
                    const payload = {
                        code,
                        redirectUri: import.meta.env.VITE_REDIRECT_URI,
                        clientId: import.meta.env.VITE_CLIENT_ID,
                        grantType: 'authorization_code'
                    };

                    const response = await AuthApiClient.post('/oauth/token', payload);

                    if (!response?.accessToken) {
                        throw new Error('Invalid token response');
                    }

                    login(
                        {
                            accessToken: response.accessToken,
                            refreshToken: response.refreshToken,
                            idToken: response.idToken,
                        },
                        response.userInfo || {}
                    );

                    tokenExchangeProcessed.current = true;
                    navigate('/dashboard');
                } catch (error) {
                    const errorMessage = error.response?.data?.message || error.message;
                    setError(`Authentication failed: ${errorMessage}`);
                    setTimeout(() => navigate('/'), 3000);
                } finally {
                    setLoading(false);
                    tokenExchangePromise.current = null;
                }
            })();

            await tokenExchangePromise.current;
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
                <div className="bg-info/10 border border-error text-error px-4 py-3 rounded">
                    <p>{error}</p>
                </div>
            ) : null}
        </div>
    );
};

export default CallbackPage;

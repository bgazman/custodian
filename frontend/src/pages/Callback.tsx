import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthApiClient } from '../api/common/AuthApiClients'; // Adjust the import path as necessary

const CallbackPage = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const handleTokenExchange = async () => {
            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
            const state = urlParams.get('state');

            if (!code || !state) {
                console.error('Missing code or state in the callback URL.');
                alert('Invalid response from the server. Please try again.');
                navigate('/'); // Redirect back to the landing page
                return;
            }

            try {
                // Prepare the request payload
                const payload = {
                    code,
                    redirectUri: import.meta.env.VITE_REDIRECT_URI || "http://localhost:5173/callback",
                    clientId: import.meta.env.VITE_CLIENT_ID || "c2ea9a3d-9ad8-42e5-a73f-488c1bc817db", // Ensure this matches your backend client ID
                    grantType: "authorization_code",
                };

                // Make the POST request using AuthApiClient
                const tokens = await AuthApiClient.post('/oauth/token', payload);

                console.log('Tokens received:', tokens);

                // Save tokens (e.g., to localStorage) and redirect to a protected page
                localStorage.setItem('accessToken', tokens.accessToken);
                if (tokens.refreshToken) {
                    localStorage.setItem('refreshToken', tokens.refreshToken);
                }
                if (tokens.idToken) {
                    localStorage.setItem('idToken', tokens.idToken);
                }

                navigate('/dashboard'); // Navigate to a protected page
            } catch (error) {
                console.error('Error exchanging token:', error);
                alert('Failed to authenticate. Please try again.');
                navigate('/');
            }
        };

        handleTokenExchange();
    }, [navigate]);

    return <div>Authenticating...</div>;
};

export default CallbackPage;

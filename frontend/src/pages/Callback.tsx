import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
const REDIRECT_URI = import.meta.env.REACT_APP_REDIRECT_URI || 'http://localhost:5173/callback';

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
                // Exchange code for tokens
                const response = await fetch('/oauth/token', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ code, redirect_uri: REDIRECT_URI }),
                });

                if (!response.ok) {
                    throw new Error('Token exchange failed');
                }

                const tokens = await response.json();
                console.log('Tokens received:', tokens);

                // Save tokens (e.g., to localStorage) and redirect to a protected page
                localStorage.setItem('accessToken', tokens.accessToken);
                navigate('/protected'); // Navigate to a protected page
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

// src/Home.tsx
import React from 'react';

const Home: React.FC = () => {
    const handleSignIn = async () => {
        const codeVerifier = generateRandomString(64);
        const codeChallenge = await generateCodeChallenge(codeVerifier);
        const state = generateRandomString(32);

        localStorage.setItem('code_verifier', codeVerifier);
        localStorage.setItem('oauth_state', state);

        const params = new URLSearchParams({
            response_type: 'code',
            client_id: import.meta.env.VITE_CLIENT_ID,
            scope: 'openid profile email admin',
            state,
            redirect_uri: import.meta.env.VITE_REDIRECT_URI,
            code_challenge: codeChallenge,
            code_challenge_method: 'S256'
        });

        window.location.href = `${import.meta.env.VITE_BACKEND_URL}/oauth/authorize?${params}`;
    };

    function generateRandomString(length: number) {
        const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        return Array.from(crypto.getRandomValues(new Uint8Array(length)))
            .map(x => possible.charAt(x % possible.length))
            .join('');
    }

    async function generateCodeChallenge(verifier: string) {
        const encoder = new TextEncoder();
        const data = encoder.encode(verifier);
        const digest = await crypto.subtle.digest('SHA-256', data);
        return btoa(String.fromCharCode(...new Uint8Array(digest)))
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=/g, '');
    }
  return (
    <div>
      <h1>Welcome to the Home Page</h1>
      <button onClick={handleSignIn}>Sign In</button>
    </div>
  );
};

export default Home;
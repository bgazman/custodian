import {useEffect} from "react";
import {useNavigate} from "react-router-dom";

function Callback() {
    const navigate = useNavigate();

    useEffect(() => {
        const queryParams = new URLSearchParams(window.location.search);
        const code = queryParams.get('code');
        const state = queryParams.get('state');

        if (code && state) {
            console.log('Authorization code:', code);
            console.log('State:', state);

            // Exchange code for tokens
            fetch('http://localhost:8080/oauth/token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    grant_type: 'authorization_code',
                    code: code,
                    redirect_uri: 'http://localhost:5173/callback',
                    client_id: 'c2ea9a3d-9ad8-42e5-a73f-488c1bc817db',
                }),
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error('Token exchange failed');
                    }
                    return response.json();
                })
                .then((data) => {
                    console.log('Tokens received:', data);

                    // Save tokens in localStorage
                    localStorage.setItem('accessToken', data.access_token);
                    localStorage.setItem('refreshToken', data.refresh_token);

                    // Redirect to the dashboard
                    navigate('/dashboard');
                })
                .catch((error) => {
                    console.error('Error during token exchange:', error);
                });
        } else {
            console.error('Missing code or state in query parameters.');
        }
    }, [navigate]);

    return <div>Processing login...</div>;
}
export default Callback;

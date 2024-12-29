import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import {useAuthentication} from "@/context/AuthenticationContext.tsx";

function LoginButton() {
    const { login } = useAuthentication();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async () => {
        try {
            await login(email, password);
            alert('Login successful!');
        } catch (error) {
            alert('Login failed. Please check your credentials.');
        }
    };

    return (
        <div>
            <input
                type="text"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={handleLogin}>Login</button>
        </div>
    );
}

export default LoginButton;

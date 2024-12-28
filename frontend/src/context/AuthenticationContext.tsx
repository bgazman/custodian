import React, { createContext, useContext, useState } from 'react';

// Create the authentication context
const AuthenticationContext = createContext();

// Custom hook for consuming the authentication context
export const useAuthentication = () => {
    return useContext(AuthenticationContext);
};

// AuthenticationProvider Component
export const AuthenticationProvider = ({ children }) => {
    const [user, setUser] = useState(null); // User object
    const [accessToken, setAccessToken] = useState(null); // Access token
    const [isAuthenticated, setIsAuthenticated] = useState(false); // Authentication state

    // Login function
    const login = async (email, password) => {
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                throw new Error('Login failed');
            }

            const data = await response.json();
            setUser(data.user);
            setAccessToken(data.accessToken);
            setIsAuthenticated(true);

            // Optionally save tokens to localStorage/sessionStorage
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('user', JSON.stringify(data.user));
        } catch (error) {
            console.error('Error during login:', error);
            throw error;
        }
    };

    // Logout function
    const logout = () => {
        setUser(null);
        setAccessToken(null);
        setIsAuthenticated(false);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
    };

    // Provide context value
    const value = {
        user,
        isAuthenticated,
        accessToken,
        login,
        logout,
    };

    return (
        <AuthenticationContext.Provider value={value}>
            {children}
        </AuthenticationContext.Provider>
    );
};

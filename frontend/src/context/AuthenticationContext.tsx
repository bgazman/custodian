import React, { createContext, useContext, useState, useEffect } from 'react';

interface AuthenticationContextType {
    user: any | null; // Adjust 'any' to a specific type if you have a defined User type
    isAuthenticated: boolean;
    login: (loginResponse: { accessToken: string; refreshToken: string; idToken: string }, userInfo: any) => void;
    logout: () => void;
}

const AuthenticationContext = createContext<AuthenticationContextType | null>(null);

export const useAuthentication = () => {
    const context = useContext(AuthenticationContext);
    if (!context) {
        throw new Error('useAuthentication must be used within an AuthenticationProvider');
    }
    return context;
};

export const AuthenticationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    // Initialize state from sessionStorage
    const [user, setUser] = useState<any>(null);
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

    // Avoid double initialization with a flag
    const [initialized, setInitialized] = useState(false);

    useEffect(() => {
        if (!initialized) {
            // Sync state with sessionStorage on first load
            const storedUser = JSON.parse(sessionStorage.getItem('user') || 'null');
            const storedAccessToken = sessionStorage.getItem('access-token');

            if (storedAccessToken) {
                setAccessToken(storedAccessToken);
                setIsAuthenticated(true);
            }

            if (storedUser) {
                setUser(storedUser);
            }

            setInitialized(true);
        }
    }, [initialized]);

    const login = (loginResponse: { accessToken: string; refreshToken: string; idToken: string }, userInfo: any) => {
        if (!loginResponse || !userInfo) {
            console.error('Invalid loginResponse or userInfo:', loginResponse, userInfo);
            return;
        }

        console.log('LOGIN_RESPONSE', loginResponse);

        sessionStorage.setItem('access-token', loginResponse.accessToken);
        sessionStorage.setItem('refresh-token', loginResponse.refreshToken);
        sessionStorage.setItem('id-token', loginResponse.idToken);
        sessionStorage.setItem('user', JSON.stringify(userInfo));

        // Sync state with sessionStorage
        setAccessToken(loginResponse.accessToken);
        setUser(userInfo);
        setIsAuthenticated(true);
    };

    const logout = () => {
        // Remove tokens and user information from sessionStorage
        sessionStorage.removeItem('access-token');
        sessionStorage.removeItem('refresh-token');
        sessionStorage.removeItem('id-token');
        sessionStorage.removeItem('user');

        // Clear state variables
        setAccessToken(null);
        setUser(null);
        setIsAuthenticated(false);
    };

    const contextValue: AuthenticationContextType = {
        user,
        isAuthenticated,
        login,
        logout,
    };

    return (
        <AuthenticationContext.Provider value={contextValue}>
            {children}
        </AuthenticationContext.Provider>
    );
};

import React, { createContext, useContext, useState, useEffect } from 'react';

interface AuthenticationContextType {
    user: any | null;
    isAuthenticated: boolean;
    login: (loginResponse: { accessToken: string; refreshToken: string; idToken: string }, userInfo: any) => void;
    logout: () => void;
    isLoading: boolean;
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
    const [user, setUser] = useState<any>(null);
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        const initializeAuth = () => {
            const storedUser = JSON.parse(localStorage.getItem('user') || 'null');
            const storedAccessToken = localStorage.getItem('access-token');

            if (storedAccessToken && storedUser) {
                setAccessToken(storedAccessToken);
                setUser(storedUser);
                setIsAuthenticated(true);
            }

            setIsLoading(false);
        };

        initializeAuth();
    }, []);

    const login = (loginResponse: { accessToken: string; refreshToken: string; idToken: string }, userInfo: any) => {
        if (!loginResponse || !userInfo) {
            console.error('Invalid loginResponse or userInfo:', loginResponse, userInfo);
            return;
        }

        console.log('LOGIN_RESPONSE', loginResponse);

        localStorage.setItem('access-token', loginResponse.accessToken);
        localStorage.setItem('refresh-token', loginResponse.refreshToken);
        localStorage.setItem('id-token', loginResponse.idToken);
        localStorage.setItem('user', JSON.stringify(userInfo));

        setAccessToken(loginResponse.accessToken);
        setUser(userInfo);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem('access-token');
        localStorage.removeItem('refresh-token');
        localStorage.removeItem('id-token');
        localStorage.removeItem('user');
        localStorage.removeItem('token_exchange_processed');
        localStorage.removeItem('oauth_state');

        setAccessToken(null);
        setUser(null);
        setIsAuthenticated(false);
    };

    const contextValue: AuthenticationContextType = {
        user,
        isAuthenticated,
        login,
        logout,
        isLoading,
    };

    if (isLoading) {
        return <div>Loading...</div>; // Or any loading indicator
    }

    return (
        <AuthenticationContext.Provider value={contextValue}>
            {children}
        </AuthenticationContext.Provider>
    );
};

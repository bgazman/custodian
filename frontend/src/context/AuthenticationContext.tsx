import React, { createContext, useContext, useState, useEffect } from 'react';

type AuthenticationContextType = {
    user: any; // Replace `any` with your user object type
    isAuthenticated: boolean;
    login: (token: string, userInfo: any) => void;
    logout: () => void;
};

const AuthenticationContext = createContext<AuthenticationContextType | null>(null);

export const useAuthentication = () => {
    const context = useContext(AuthenticationContext);
    if (!context) {
        throw new Error('useAuthentication must be used within an AuthenticationProvider');
    }
    return context;
};

export const AuthenticationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<any>(JSON.parse(localStorage.getItem('user') || 'null'));
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem('access-token'));
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!accessToken);

    const login = (token: string, userInfo: any) => {
        localStorage.setItem('access-token', token);
        localStorage.setItem('user', JSON.stringify(userInfo));
        setAccessToken(token);
        setUser(userInfo);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem('access-token');
        localStorage.removeItem('user');
        setAccessToken(null);
        setUser(null);
        setIsAuthenticated(false);
    };

    const contextValue: AuthenticationContextType = {
        user,
        isAuthenticated,
        login,
        logout
    };

    return (
        <AuthenticationContext.Provider value={contextValue}>
            {children}
        </AuthenticationContext.Provider>
    );
};

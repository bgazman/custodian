import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthentication } from './Main/context/AuthenticationContext';
import LandingPage from '../pages/LandingPage';

export const LandingRoute = () => {
    const { isAuthenticated } = useAuthentication();

    if (isAuthenticated) {
        return <Navigate to="/dashboard" replace />;
    }

    return <LandingPage />;
};

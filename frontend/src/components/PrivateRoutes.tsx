import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthentication } from './Main/context/AuthenticationContext';

const PrivateRoute = ({ children }) => {
    const { isAuthenticated } = useAuthentication();

    if (!isAuthenticated) {
        // Redirect to the landing page or a login page within your app
        return <Navigate to="/" replace />;
    }

    return children;
};

export default PrivateRoute;

import {useAuthentication} from '../context/AuthenticationContext';
import {buildAuthUrl} from "../utils/AuthUtils.ts";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
const CLIENT_ID = import.meta.env.VITE_CLIENT_ID ;
const REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI;
const BASE_URL = import.meta.env.VITE_BACKEND_URL;
const PrivateRoute = ({ children }) => {
    const { isAuthenticated } = useAuthentication();
    const navigate = useNavigate();

    useEffect(() => {
        if (!isAuthenticated) {
            const authUrl = buildAuthUrl(BASE_URL, CLIENT_ID, REDIRECT_URI);

            // Perform navigation only if not already navigating
            navigate(authUrl, { replace: true });
        }
    }, [isAuthenticated]); // Removed `navigate` from the dependency array

    if (!isAuthenticated) {
        return null; // Render nothing while navigating
    }

    return children;
};

export default PrivateRoute;
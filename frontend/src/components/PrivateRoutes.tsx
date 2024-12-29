import {useAuthentication} from '../context/AuthenticationContext';
import {buildAuthUrl} from "../utils/AuthUtils.ts";
import {useNavigate} from "react-router-dom";
const CLIENT_ID = import.meta.env.REACT_APP_CLIENT_ID || 'c2ea9a3d-9ad8-42e5-a73f-488c1bc817db';
const REDIRECT_URI = import.meta.env.REACT_APP_REDIRECT_URI || 'http://localhost:5173/callback';
const BASE_URL = import.meta.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';
const PrivateRoute = ({ children }) => {
    const { isAuthenticated } = useAuthentication();
    const navigate = useNavigate(); // Correct usage

    if (!isAuthenticated) {
        // Instead of using window.location.href, you can use navigate
        navigate(buildAuthUrl(BASE_URL, CLIENT_ID, REDIRECT_URI));
        return null;
    }

    return children;
};

export default PrivateRoute;

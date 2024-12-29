import { useAuthentication } from '../context/AuthenticationContext';

const useAuth = () => {
    const { refreshAccessToken, logout, isAuthenticated } = useAuthentication();

    return { refreshToken: refreshAccessToken, logout, isAuthenticated };
};

export default useAuth;
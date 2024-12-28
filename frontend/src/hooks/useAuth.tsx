const useAuth = () => {
    const [token, setToken] = useState(localStorage.getItem("access-token"));

    const refreshToken = async () => {
        try {
            const response = await fetch("/api/auth/refresh", {
                method: "POST",
                headers: { "Authorization": `Bearer ${localStorage.getItem("refresh-token")}` },
            });

            if (response.ok) {
                const { accessToken } = await response.json();
                localStorage.setItem("access-token", accessToken);
                setToken(accessToken);
            } else {
                throw new Error("Token refresh failed");
            }
        } catch (error) {
            console.error(error);
            logout();
        }
    };

    const logout = () => {
        localStorage.removeItem("access-token");
        localStorage.removeItem("refresh-token");
        setToken(null);
    };

    return { token, refreshToken, logout };
};

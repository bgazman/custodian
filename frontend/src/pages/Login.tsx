import React, {useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {CustodianLoginService} from "../api/CustodianLoginService.tsx";
const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("access-token");
        if (token) {
            navigate("/dashboard", { replace: true });
        }
    }, []);


    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await CustodianLoginService.login(email, password);
            console.log('Login response:', response); // Debug log
            if (response.success && response.data) {
                localStorage.setItem("access-token", response.data.accessToken);
                localStorage.setItem("refresh-token", response.data.refreshToken);
                localStorage.setItem("role", response.data.roles);
                navigate("/dashboard", { replace: true }); // Added replace:true
            } else {
                setError(response.message);
            }
        } catch (err) {
            console.error('Login error:', err); // Debug log
            setError(err.message);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-md">
                <h2 className="text-2xl font-bold mb-4">Login</h2>
                {error && <div className="text-red-500 text-sm mb-2">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Email</label>
                        <input
                            type="text"
                            className="w-full border border-gray-300 p-2 rounded"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Password</label>
                        <input
                            type="password"
                            className="w-full border border-gray-300 p-2 rounded"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600"
                    >
                        Login
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;

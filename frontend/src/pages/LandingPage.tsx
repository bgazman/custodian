import { useNavigate } from "react-router-dom";
import { buildAuthUrl } from "../utils/AuthUtils.ts";
import { useState } from "react";
import { useAuthentication } from '../context/AuthenticationContext';

const CLIENT_ID = import.meta.env.VITE_CLIENT_ID;
const REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI;
const BASE_URL = import.meta.env.VITE_BACKEND_URL;

// Ensure critical environment variables are defined
if (!CLIENT_ID || !REDIRECT_URI || !BASE_URL) {
    throw new Error('Missing required environment variables for OAuth configuration.');
}

const LandingPage = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);

    const handleSignIn = () => {
        if (isLoading) return; // Prevent multiple clicks
        setIsLoading(true);
        try {
            const authUrl = buildAuthUrl(BASE_URL, CLIENT_ID, REDIRECT_URI);
            console.log("Redirecting to IDP...");
            window.location.href = authUrl; // Redirects to IdP
        } catch (error) {
            console.error('Error during sign-in redirection:', error);
            alert('An error occurred. Please try again later.');
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1 className="text-xl font-bold">Crypto Custodian</h1>
                    <button
                        className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
                        onClick={handleSignIn}
                    >
                        Sign In
                    </button>
                </div>
            </nav>
            <main className="max-w-7xl mx-auto px-4 py-16">
                <div className="text-center">
                    <h2 className="text-4xl font-bold text-gray-900">Secure User Management</h2>
                    <p className="mt-4 text-xl text-gray-600">Manage your users securely</p>
                </div>
            </main>
        </div>
    );
};

export default LandingPage;

import { useNavigate } from "react-router-dom";

const LandingPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1 className="text-xl font-bold">Crypto Custodian</h1>
                    <button
                        className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
                        onClick={() => navigate("/login")}
                    >
                        Sign In
                    </button>
                </div>
            </nav>
            <main className="max-w-7xl mx-auto px-4 py-16">
                <div className="text-center">
                    <h2 className="text-4xl font-bold text-gray-900">Secure Crypto Management</h2>
                    <p className="mt-4 text-xl text-gray-600">Manage your digital assets with confidence</p>
                </div>
            </main>
        </div>
    );
};

export default LandingPage;

import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Dashboard from "./pages/Dashboard";

import LandingPage from "./pages/LandingPage";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import AdminPage from './pages/Admin';
import IamDashboard from "./pages/IamDashboard.tsx";
import GroupsPage from "./pages/Groups";
import GroupMembershipsPage from "./pages/GroupMembership";

// In your router configuration

const PrivateRoute = ({ children }) => {
    const token = localStorage.getItem("access-token");
    return isTokenValid(token) ? children : <Navigate to="/login" replace />;
};

const isTokenValid = (token) => {
    try {
        const payload = JSON.parse(atob(token.split(".")[1])); // Decode JWT
        return Date.now() < payload.exp * 1000; // Check expiry
    } catch {
        return false; // Invalid token format
    }
};
const AppLayout = () => (

    <div className="min-h-screen bg-gray-100">
        <Navbar />
        <main className="pt-16 h-full">
            <Routes>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/iam-dashboard" element={<IamDashboard />} />
                <Route path="/" element={<Navigate to="/dashboard" />} /> {/* Default authenticated route */}

            </Routes>
        </main>
    </div>
);

function App() {
    return (

        <BrowserRouter>
            <Routes>
                {/* Landing Page (Default Route) */}
                <Route path="/" element={<LandingPage />} />

                {/* Login Route */}
                <Route path="/login" element={<Login />} />

                {/* Private Routes */}
                <Route
                    path="/*"
                    element={
                        <PrivateRoute>
                            <AppLayout />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </BrowserRouter>

            );
}

export default App;
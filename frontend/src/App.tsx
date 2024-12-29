import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Dashboard from "./pages/Dashboard";

import LandingPage from "./pages/LandingPage";
import Navbar from "./components/Navbar";
import AdminPage from './pages/Admin';
import IamDashboard from "./pages/IamDashboard";
import Callback from "./pages/Callback";
import {AuthenticationProvider} from "./context/AuthenticationContext";
import PrivateRoute from "./components/PrivateRoutes";


const AppLayout = () => (
    <div className="min-h-screen bg-gray-100">
        <Navbar />
        <main className="pt-16 h-full">
            <Routes>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/iam-dashboard" element={<IamDashboard />} />
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
                    <Route path="/callback" element={<Callback />} />

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
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import {LandingRoute} from "./components/LandingRoute";
import Navbar from "./components/Navbar";
import AdminPage from './pages/Admin';
import IamDashboard from "./pages/IamDashboard";
import Callback from "./pages/Callback";
import { AuthenticationProvider } from "./context/AuthenticationContext";
import PrivateRoute from "./components/PrivateRoutes";

const AppLayout = () => (
    <div className="min-h-screen bg-gray-100">
        <Navbar />
        <main className="pt-16 h-full">
            <Routes>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/iam-dashboard" element={<IamDashboard />} />
                <Route path="/admin" element={<AdminPage />} />
            </Routes>
        </main>
    </div>
);

function App() {
    return (
        <AuthenticationProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<LandingRoute  />} />
                    <Route path="/callback" element={<Callback />} />
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
        </AuthenticationProvider>
    );
}

export default App;

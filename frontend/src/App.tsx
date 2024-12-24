import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Wallets from "./pages/Wallets";
import Portfolio from "./pages/Portfolio";
import Transactions from "./pages/Transactions";
import LandingPage from "./pages/LandingPage";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import AdminPage from './pages/Admin';
import UsersPage from "./pages/Users";
import GroupsPage from "./pages/Groups";
import GroupMembershipsPage from "./pages/GroupMembership.tsx";

// In your router configuration

const PrivateRoute = ({ children }) => {
    const isAuthenticated = !!localStorage.getItem("access-token");
    return isAuthenticated ? children : <Navigate to="/login" replace />;
};


const AppLayout = () => (

    <div className="min-h-screen bg-gray-100">
        <Navbar />
        <main className="pt-16 h-full">
            <Routes>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/wallets" element={<Wallets />} />
                <Route path="/portfolio" element={<Portfolio />} />
                <Route path="/transactions" element={<Transactions />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/users" element={<UsersPage />} />
                <Route path="/groups" element={<GroupsPage />} />
                <Route path="/groups-membership" element={<GroupMembershipsPage />} />

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
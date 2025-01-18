import { BrowserRouter, Route, Routes } from "react-router-dom";
import MainDashboard from "./pages/MainDashboard";
import {LandingRoute} from "./components/LandingRoute";
import MainNavbar from "./components/Main/MainNavbar";
import AdminPage from './pages/Admin';
import IamDashboard from "./pages/IamDashboard";
import Callback from "./pages/Callback";
import { AuthenticationProvider } from "./context/AuthenticationContext";
import PrivateRoute from "./components/PrivateRoutes";
import UserDetailsPage from "./pages/UserDetails";



const AppLayout = () => (
    <div className="min-h-screen bg-background">
        <MainNavbar />
        <main className="pt-16 h-full text-text">
            <Routes>
                <Route path="/dashboard" element={<MainDashboard />} />
                <Route path="/iam-dashboard" element={<IamDashboard />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/users/:id" element={<UserDetailsPage />} />
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
                    <Route path="/oauth-callback" element={<Callback />} />
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

import { BrowserRouter, Route, Routes } from "react-router-dom";
import { LandingRoute } from "./components/LandingRoute";
import Callback from "./pages/Callback";
import { AuthenticationProvider } from "./context/AuthenticationContext";
import PrivateRoute from "./components/PrivateRoutes";
import Layout from "./components/Main/Layout";
import Header from "./components/Main/Header";
import Sidebar from "./components/Main/Sidebar";
import MainContent from "./components/Main/MainContent";
import { navigationItems } from './config/navigation';
import UserMenu from "./components/Main/UserMenu";
import React, {useEffect, useState} from 'react';
import {useLayout} from "./context/LayoutContext";
import LayoutSwitcher from "./components/Main/LayoutSwitcher";
import ThemeSwitcher from "./components/Main/ThemeSwitcher";
import IamDashboard from "./pages/IamDashboard";
import AdminPage from "./pages/Admin";
import UserDetailsPage from "./pages/UserDetails";

const AppLayout = () => {
    const { layout } = useLayout();
    const [isOpen, setIsOpen] = useState(false);

    return (
        <Layout>
            <Header
                logoText="My App"
                showSearch={true}
                onCollapse={() => setIsOpen(!isOpen)}
                rightContent={<>
                    <UserMenu username="John Doe" email="john.doe@example.com" />
                    <LayoutSwitcher/>
                    <ThemeSwitcher/>
                </>}
                position={layout === 'sidebar-right' ? 'right' : 'left'}
            />

            {(layout === 'sidebar-left' || layout === 'sidebar-right') && (
                <Sidebar
                    items={navigationItems}
                    isOpen={isOpen}
                    onToggle={() => setIsOpen(!isOpen)}
                    position={layout === 'sidebar-right' ? 'right' : 'left'}
                />
            )}

            <MainContent  isOpen={isOpen}>
                <Routes>
                    <Route path="/iam-dashboard" element={<IamDashboard />} />
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/users/:id" element={<UserDetailsPage />} />
                </Routes>
            </MainContent>
        </Layout>
    );
};

function App() {
    return (
        <AuthenticationProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<LandingRoute />} />
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

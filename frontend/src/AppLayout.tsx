import {useLayout} from "./components/Main/context/LayoutContext";
import React, {useCallback, useState} from "react";

import UserMenu from "./components/Main/header/components/UserMenu";
import LayoutSwitcher from "./components/Main/layout/components/layout-switcher/LayoutSwitcher";
import ThemeSwitcher from "./components/Main/theme/components/theme-switcher/ThemeSwitcher";
import MainContent from "./components/Main/main/MainContent";
import {Route, Routes} from "react-router-dom";
import IamDashboard from "./pages/IamDashboard";
import AdminPage from "./pages/Admin";
import UserDetailsPage from "./pages/UserDetails";
 import navigationItems from "./components/Main/config/navigation";
import Navigation from "./components/Main/navigation/Navigation";
import Header from "./components/Main/header/Header";
import Layout from "./components/Main/layout/Layout";


const AppLayout = () => {
    const { layout } = useLayout();
    const [isOpen, setIsOpen] = useState(false);

    const toggleNavigation = useCallback(() => {
        setIsOpen(prev => !prev);
    }, []);



    return (
        <Layout data-layout={layout} >
            <Header
                logoText="My App"
                showSearch={true}
                onToggle={toggleNavigation}  // Pass the function reference, not the result
                rightContent={
                    <>
                        <UserMenu username="John Doe" email="john.doe@example.com" />
                        <LayoutSwitcher />
                        <ThemeSwitcher />
                    </>
                }
            />

            <Navigation
                items={navigationItems}
                isOpen={isOpen}
                onToggle={toggleNavigation}
                closeOnSelect={true}
            />


            <MainContent

            >
                <Routes>
                    <Route path="/iam-dashboard" element={<IamDashboard/>}/>
                    <Route path="/admin" element={<AdminPage/>}/>
                    <Route path="/users/:id" element={<UserDetailsPage/>}/>
                </Routes>
            </MainContent>
        </Layout>
    );
};

export default AppLayout;
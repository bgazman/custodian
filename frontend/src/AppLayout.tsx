import {useLayout} from "./components/Main/context/LayoutContext";
import React from "react";
import MainContent from "./components/Main/content/MainContent";
import {Route, Routes} from "react-router-dom";
import AdminPage from "./pages/Admin";
import navigationItems from "./components/Main/config/navigation";
import Header from "./components/Main/header/Header";
import Layout from "./components/Main/layout/Layout";
import Sidebar from "./components/Main/sidebar/Sidebar.tsx";
import Topbar from "./components/Main/topbar/Topbar.tsx";
import {Navigation} from "./components/Main/navigation/Navigation";

const AppLayout = () => {
    const { layout } = useLayout(); // Returns one of ["top", "sidebar-left", "sidebar-right"]

    return (
        <Layout
            layout={layout}
            topbarSlot={
                (layout === "sidebar-left" || layout === "sidebar-right") ? (
                    <Topbar>
                        <Header logo="MyApp" />

                    </Topbar>
                ) :
                    <Topbar>
                        <Navigation items={navigationItems} />

                    </Topbar>
            }
            sidebarSlot={
                (layout === "sidebar-left" || layout === "sidebar-right") ? (
                    <Sidebar>
                        <Navigation items={navigationItems} />
                    </Sidebar>
                ) : null
            }
            contentSlot={
                <MainContent>
                    <Routes>
                        <Route path="/admin" element={<AdminPage />} />
                    </Routes>
                </MainContent>
            }
        />
    );
};

export default AppLayout;

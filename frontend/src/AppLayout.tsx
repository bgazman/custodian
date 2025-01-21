import {useLayout} from "./components/Main/context/LayoutContext";
import React from "react";
import MainContent from "./components/Main/main/MainContent";
import {Route, Routes} from "react-router-dom";
import AdminPage from "./pages/Admin";
import navigationItems from "./components/Main/config/navigation";
import Header from "./components/Main/header/Header";
import Layout from "./components/Main/layout/Layout";
import Aside from "./components/Main/aside/Aside.tsx";
import {Navigation} from "./components/Main/navigation/Navigation";

const AppLayout = () => {
    const { layout } = useLayout(); // Returns one of ["top", "aside-left", "aside-right"]

    return (
        <Layout
            layout={layout}
            headerSlot={
                (layout === "aside-left" || layout === "aside-right") ? (
                        <Header>

                        </Header>
                ) :
                    <Header>
                        <Navigation items={navigationItems} />

                    </Header>
            }
            asideSlot={
                (layout === "aside-left" || layout === "aside-right") ? (
                    <Aside>
                        <Navigation items={navigationItems} />
                    </Aside>
                ) : null
            }
            mainSlot={
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

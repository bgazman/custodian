import {useLayout} from "./components/Main/context/LayoutContext";
import React, {useCallback, useState} from "react";
import MainContent from "./components/Main/main/MainContent";
import {Route, Routes} from "react-router-dom";
import AdminPage from "./pages/Admin";
 import navigationItems from "./components/Main/config/navigation";
import Navigation from "./components/Main/navigation/Navigation";
import Header from "./components/Main/header/Header";
import Layout from "./components/Main/layout/Layout";


const AppLayout = () => {
    const { layout } = useLayout();





    return (
        <Layout layout={layout} >

            <Header
                logoText="Your App Name"
            />
            <Navigation items={navigationItems} />
            <MainContent

            >
                <Routes>
                    <Route path="/admin" element={<AdminPage/>}/>
                </Routes>
            </MainContent>
        </Layout>
    );
};

export default AppLayout;
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { LandingRoute } from "./components/LandingRoute";
import Callback from "./pages/Callback";
import { AuthenticationProvider } from "./components/Main/context/AuthenticationContext";
import React from 'react';

import AppLayout from "./AppLayout";



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
                        // <PrivateRoute>
                                <AppLayout />

                        }
                    />
                </Routes>
            </BrowserRouter>
        </AuthenticationProvider>
    );
}

export default App;

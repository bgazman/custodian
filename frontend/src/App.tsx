import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './sdk/home';
import Login from './login';
import ForgotPassword from './forgot-password';
import OAuthCallback from './sdk/oauth-callback';
import Dashboard from './dashboard';
import Mfa from './mfa';
import Consent from "./consent";

const App: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // You can set isAuthenticated based on your authentication logic

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/mfa" element={<Mfa />} />
        <Route path="/consent" element={<Consent />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/oauth-callback" element={<OAuthCallback />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Router>
  );
};

export default App;
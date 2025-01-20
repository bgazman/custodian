import React, { useState } from 'react';
import {Users, UserCircle, Shield, Key, Settings, FileJson} from 'lucide-react';
import { useNavigate } from "react-router-dom";
import UsersComponent from "../components/Users/Users";
import Groups from "../components/Groups/Groups";
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import Permissions from "../components/Permissions/Permissions";
import Secrets from "../components/Secrets/Secrets";

const IamDashboard = () => {
    const [currentSection, setCurrentSection] = useState('users');
    const [error, setError] = useState('');

    const apiUrl = `${import.meta.env.VITE_BACKEND_URL}/v3/api-docs`;

    const renderContent = () => {
        try {
            switch(currentSection) {
                case 'users':
                    return <UsersComponent />;
                case 'groups':
                    return <Groups />;
                case 'api-docs':
                    return <SwaggerUI url={apiUrl} />;
                case 'permissions':
                    return <Permissions />;
                case 'secrets':
                    return <Secrets />;
                default:
                    return (
                        <div className="flex items-center justify-center h-64">
                            <p className="text-text">
                                {currentSection === 'tokens' && 'Token configuration coming soon'}
                            </p>
                        </div>
                    );
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
            return null;
        }
    };

    const navigationItems = [
        { name: 'Users', path: 'users', icon: <UserCircle className="w-5 h-5" /> },
        { name: 'Groups', path: 'groups', icon: <Users className="w-5 h-5" /> },
        { name: 'Permissions', path: 'permissions', icon: <Shield className="w-5 h-5" /> },
        { name: 'Secrets', path: 'secrets', icon: <Key className="w-5 h-5" /> },
        { name: 'Token Configurations', path: 'tokens', icon: <Settings className="w-5 h-5" /> },
        { name: 'API Docs', path: 'api-docs', icon: <FileJson className="w-5 h-5" /> }
    ];

    return (
        <div className="relative min-h-screen bg-background">
            <div className="relative pt-28">
                {/* IAM Dashboard Navbar */}
                <nav className="bg-background fixed top-16 left-0 w-full border-b border-border z-20">
                <div className="px-4">
                    <div className="flex h-16 items-center justify-between">
                        <div className="flex space-x-8">
                            {navigationItems.map((item) => (
                                <button
                                    key={item.path}
                                    onClick={() => {
                                        setError('');
                                        setCurrentSection(item.path);
                                    }}
                                    className={`inline-flex items-center px-1 pt-1 text-sm font-medium
                                       ${currentSection === item.path
                                        ? 'border-b-2 border-primary text-text-dark'
                                        : 'text-text-muted hover:border-primary hover:text-text'}`}
                                >
                                    <span className="mr-2">{item.icon}</span>
                                    {item.name}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            </nav>

            <main className="mt-4">
                {error && (
                    <div className="bg-error/10 border border-error text-error px-4 py-3 rounded mb-4">
                        <span className="block sm:inline">{error}</span>
                    </div>
                )}
                {renderContent()}
            </main>
        </div>
</div>
);
};

export default IamDashboard;
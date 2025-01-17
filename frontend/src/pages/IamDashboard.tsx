import React, { useState } from 'react';
import {Users, UserCircle, Shield, Key, Settings, FileJson} from 'lucide-react';
import { useNavigate } from "react-router-dom";
import UsersComponent from "../components/Users/Users";
import Groups from "../components/Groups/Groups";
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import Permissions from "../components/Permissions/Permissions";

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
                default:
                    return (
                        <div className="flex items-center justify-center h-64">
                            <p className="text-gray-500">
                                {currentSection === 'secrets' && 'Secrets management coming soon'}
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
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white border-b border-gray-200">
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
                                        ? 'border-b-2 border-indigo-500 text-gray-900'
                                        : 'text-gray-500 hover:border-gray-300 hover:text-gray-700'}`}
                                >
                                    <span className="mr-2">{item.icon}</span>
                                    {item.name}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            </nav>

            <main className="p-6">
                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                        <span className="block sm:inline">{error}</span>
                    </div>
                )}
                {renderContent()}
            </main>
        </div>
    );
};

export default IamDashboard;
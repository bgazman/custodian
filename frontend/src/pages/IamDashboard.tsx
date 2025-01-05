import React, { useEffect, useState } from 'react';
import { Users, UserCircle, Shield, Key, Settings } from 'lucide-react';
import { useNavigate } from "react-router-dom";
import UsersComponent from "../components/Users/Users";
import Groups from "../components/Groups/Groups";

const IamDashboard = () => {
    const navigate = useNavigate();
    const [currentSection, setCurrentSection] = useState('users');
    const [error, setError] = useState('');

    useEffect(() => {
        // const isAdmin = localStorage.getItem("role") === "ADMIN";
        // if (!isAdmin) {
        //     navigate("/", { replace: true });
        // }
    }, [navigate]);

    const renderContent = () => {
        try {
            switch(currentSection) {
                case 'users':
                    return <UsersComponent />;
                case 'groups':
                    return <Groups />;
                case 'permissions':
                    return (
                        <div className="flex items-center justify-center h-64">
                            <p className="text-gray-500">Permissions management coming soon</p>
                        </div>
                    );
                case 'secrets':
                    return (
                        <div className="flex items-center justify-center h-64">
                            <p className="text-gray-500">Secrets management coming soon</p>
                        </div>
                    );
                case 'tokens':
                    return (
                        <div className="flex items-center justify-center h-64">
                            <p className="text-gray-500">Token configuration coming soon</p>
                        </div>
                    );
                default:
                    return (
                        <div className="flex items-center justify-center h-64">
                            <p className="text-gray-500">Select a section from the navigation above</p>
                        </div>
                    );
            }
        } catch (err) {
            setError(err.message);
            return null;
        }
    };

    const navigationItems = [
        { name: 'Users', path: 'users', icon: <UserCircle className="w-5 h-5" /> },
        { name: 'Groups', path: 'groups', icon: <Users className="w-5 h-5" /> },
        { name: 'Permissions', path: 'permissions', icon: <Shield className="w-5 h-5" /> },
        { name: 'Secrets', path: 'secrets', icon: <Key className="w-5 h-5" /> },
        { name: 'Token Configurations', path: 'tokens', icon: <Settings className="w-5 h-5" /> }
    ];

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white border-b border-gray-200   ">
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
                                        : 'text-gray-500 hover:border-gray-300 hover:text-gray-700'
                                    }`}
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
                    <Alert variant="destructive" className="mb-4">
                        <AlertDescription>{error}</AlertDescription>
                    </Alert>
                )}
                {renderContent()}
            </main>
        </div>
    );
};

export default IamDashboard;
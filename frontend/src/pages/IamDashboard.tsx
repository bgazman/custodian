import React, {useEffect, useState} from 'react';
import { Users, UserCircle, Shield, Key, Settings } from 'lucide-react';
import {useNavigate} from "react-router-dom";
import UsersComponent from "../components/Users/Users";

const IamDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [currentSection, setCurrentSection] = useState('users');
    useEffect(() => {
        const isAdmin = localStorage.getItem("role") === "ADMIN";
        if (!isAdmin) {
            navigate("/", { replace: true });
        }
    }, [navigate]);

    const renderContent = () => {
        switch(currentSection) {
            case 'users': return <UsersComponent />;
            case 'groups': return <div>Groups Content</div>;
            case 'permissions': return <div>Permissions Content</div>;
            case 'secrets': return <div>Secrets Content</div>;
            case 'tokens': return <div>Tokens Content</div>;
            default: return <IamDashboard />;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white border-b border-gray-200">
                <div className="px-4">
                    <div className="flex h-16 items-center justify-between">
                        <div className="flex space-x-8">
                            {[
                                { name: 'Users', path: 'users', icon: <UserCircle className="w-5 h-5" /> },
                                { name: 'Groups', path: 'groups', icon: <Users className="w-5 h-5" /> },
                                { name: 'Permissions', path: 'permissions', icon: <Shield className="w-5 h-5" /> },
                                { name: 'Secrets', path: 'secrets', icon: <Key className="w-5 h-5" /> },
                                { name: 'Token Configurations', path: 'tokens', icon: <Settings className="w-5 h-5" /> }
                            ].map((item) => (
                                <button
                                    key={item.path}
                                    onClick={() => setCurrentSection(item.path)}
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
                {renderContent()}
            </main>
        </div>
    );
};

const UsersDashboardLayout = () => (
    <>
        <div className="mb-4 text-sm text-gray-600">
            Home &gt; Users
        </div>
        <div className="mb-6">
            <h1 className="text-2xl font-semibold text-gray-900">Users Management</h1>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-sm mb-6">
            <div className="flex gap-4 items-center">
                {/* Filter components */}
            </div>
        </div>
        <div className="bg-white rounded-lg shadow-sm">
            {/* Users table */}
        </div>
    </>
);

export default IamDashboard;
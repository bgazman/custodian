import React from 'react';
import { Users, UserCircle, Shield, Key, Settings } from 'lucide-react';

const UserDashboardNavbar = ({ activePath = '/dashboard' }) => {
    const navItems = [
        { name: 'Dashboard', path: '/dashboard', icon: <Users className="w-5 h-5" /> },
        { name: 'Users', path: '/users', icon: <UserCircle className="w-5 h-5" /> },
        { name: 'Groups', path: '/groups', icon: <Users className="w-5 h-5" /> },
        { name: 'Permissions', path: '/permissions', icon: <Shield className="w-5 h-5" /> },
        { name: 'Secrets', path: '/secrets', icon: <Key className="w-5 h-5" /> },
        { name: 'Token Configurations', path: '/tokens', icon: <Settings className="w-5 h-5" /> }
    ];

    return (
        <nav className="bg-white border-b border-gray-200">
            <div className="px-4">
                <div className="flex h-16 items-center justify-between">
                    <div className="flex space-x-8">
                        {navItems.map((item) => {
                            const isActive = activePath === item.path;
                            return (
                                <button
                                    key={item.path}
                                    onClick={() => console.log(`Navigate to: ${item.path}`)}
                                    className={`inline-flex items-center px-1 pt-1 text-sm font-medium 
                    ${isActive
                                        ? 'border-b-2 border-indigo-500 text-gray-900'
                                        : 'text-gray-500 hover:border-gray-300 hover:text-gray-700'
                                    }`}
                                >
                                    <span className="mr-2">{item.icon}</span>
                                    {item.name}
                                </button>
                            );
                        })}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default UserDashboardNavbar;
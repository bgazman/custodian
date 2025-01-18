import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, X, Bell, User, LogOut } from 'lucide-react';
import { useAuthentication } from '../context/AuthenticationContext';

const MainNavbar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { logout } = useAuthentication();
    const navigate = useNavigate();
    const isAdmin = true;

    const handleLogout = () => {
        logout();
        setTimeout(() => navigate('/', { replace: true }), 100);
    };

    const commonLinks = [];
    const adminLinks = [{ path: '/iam-dashboard', label: 'IAM Dashboard' }];
    const links = isAdmin ? [...commonLinks, ...adminLinks] : commonLinks;

    return (
        <>
            {/* Overlay */}
            {isOpen && (
                <div
                    className="fixed inset-0 bg-black/50 z-40"
                    onClick={() => setIsOpen(false)}
                />
            )}

            {/* Sidebar */}
            <aside
                className={`fixed top-0 left-0 h-full w-64 bg-gray-800 transform transition-transform duration-300 ease-in-out z-50 ${
                    isOpen ? 'translate-x-0' : '-translate-x-full'
                }`}
            >
                <div className="p-4">
                    <button
                        onClick={() => setIsOpen(false)}
                        className="text-gray-400 hover:text-white"
                    >
                        <X className="h-6 w-6" />
                    </button>
                    <nav className="mt-4 space-y-2">
                        {links.map((link) => (
                            <Link
                                key={link.path}
                                to={link.path}
                                className="text-gray-300 hover:text-white block px-3 py-2 rounded-md hover:bg-gray-700"
                                onClick={() => setIsOpen(false)}
                            >
                                {link.label}
                            </Link>
                        ))}
                    </nav>
                </div>
            </aside>

            {/* Navbar */}
            <header className="fixed top-0 left-0 right-0 h-16 bg-gray-800 shadow z-30">
                <div className="px-4 h-full">
                    <div className="flex items-center justify-between h-full">
                        <div className="flex items-center">
                            <button
                                onClick={() => setIsOpen(!isOpen)}
                                className="text-gray-400 hover:text-white p-2 rounded-md hover:bg-gray-700"
                            >
                                <Menu className="h-6 w-6" />
                            </button>
                            <Link
                                to="/dashboard"
                                className="text-white text-xl font-bold ml-4"
                            >
                                Custodian
                            </Link>
                        </div>
                        <div className="flex items-center space-x-4">
                            <button className="text-gray-400 hover:text-white p-2 rounded-md hover:bg-gray-700">
                                <Bell className="h-6 w-6" />
                            </button>
                            <button className="text-gray-400 hover:text-white p-2 rounded-md hover:bg-gray-700">
                                <User className="h-6 w-6" />
                            </button>
                            <button
                                onClick={handleLogout}
                                className="text-gray-400 hover:text-white p-2 rounded-md hover:bg-gray-700"
                            >
                                <LogOut className="h-6 w-6" />
                            </button>
                        </div>
                    </div>
                </div>
            </header>
        </>
    );
};

export default MainNavbar;
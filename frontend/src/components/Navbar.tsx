import React, { useState } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {Menu, X, Bell, User, LogOut, Settings} from 'lucide-react';

const Navbar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const navigate = useNavigate();
    const isAdmin = localStorage.getItem('role') === 'ADMIN';

    const handleLogout = () => {
        localStorage.clear();
        navigate("/login", { replace: true });
    };

    return (
        <>
            {isOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-40" onClick={() => setIsOpen(false)} />
            )}

            <div className={`fixed top-0 left-0 h-full w-64 bg-gray-800 transform transition-transform z-50 ${isOpen ? 'translate-x-0' : '-translate-x-full'}`}>
                <div className="p-4">
                    <button onClick={() => setIsOpen(false)} className="text-gray-400 hover:text-white">
                        <X className="h-6 w-6" />
                    </button>
                    <div className="mt-4 space-y-2">
                        <Link
                            to="/dashboard"
                            className="text-gray-300 hover:text-white block px-3 py-2"
                            onClick={() => setIsOpen(false)}
                        >
                            Dashboard
                        </Link>                        <Link to="/wallets" className="text-gray-300 hover:text-white block px-3 py-2" onClick={() => setIsOpen(false)}>Wallets</Link>
                        <Link to="/portfolio" className="text-gray-300 hover:text-white block px-3 py-2" onClick={() => setIsOpen(false)}>Portfolio</Link>
                        <Link to="/transactions" className="text-gray-300 hover:text-white block px-3 py-2" onClick={() => setIsOpen(false)}>Transactions</Link>
                    </div>
                </div>
            </div>

            <nav className="fixed top-0 left-0 right-0 h-16 bg-gray-800 shadow z-50">
                <div className="max-w-7xl mx-auto px-4">
                    <div className="flex items-center justify-between h-16">
                        <div className="flex items-center">
                            <button onClick={() => setIsOpen(!isOpen)} className="text-gray-400 hover:text-white">
                                <Menu className="h-6 w-6" />
                            </button>
                            <Link to="/dashboard" className="text-white text-xl font-bold ml-4">Crypto Custodian</Link>
                        </div>
                        <div className="flex items-center space-x-4">
                            {isAdmin && (
                                <Link
                                    to="/admin"
                                    className="text-gray-400 hover:text-white"
                                    // onClick={(e) => {
                                    //     e.preventDefault();
                                    //     navigate("/admin");
                                    // }}
                                >
                                    <Settings className="h-6 w-6" />
                                </Link>

                            )}
                            <button className="text-gray-400 hover:text-white">
                                <Bell className="h-6 w-6" />
                            </button>
                            <button className="text-gray-400 hover:text-white">
                                <User className="h-6 w-6" />
                            </button>
                            <button
                                onClick={handleLogout}
                                className="text-gray-400 hover:text-white"
                            >
                                <LogOut className="h-6 w-6" />
                            </button>
                        </div>
                    </div>
                </div>
            </nav>
        </>
    );
};


export default Navbar;
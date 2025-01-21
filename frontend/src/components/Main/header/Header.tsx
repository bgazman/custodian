import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom';

import {LogOut} from "lucide-react";
import UserMenu from "./components/UserMenu";
import './header.css';

interface HeaderProps {
    logoText
}

interface HeaderProps {
    logoText?: string
}

const Header: React.FC<HeaderProps> = ({
                                           logoText,
                                       }) => {

    return (
        <header className={`header`}>
            <div className="px-4 h-full flex items-center justify-between">
                {/* Left Side */}
                <div className="flex items-center">

                    <Link to="/dashboard" className="text-text text-xl font-bold ml-4">
                        {logoText}
                    </Link>
                </div>

                <div className="flex items-center space-x-4">
                    <UserMenu username="John Doe" email="john.doe@example.com"/>
                    <button

                    >
                        <LogOut className="h-6 w-6"/>
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Header;
import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom';

import {HeaderProps} from './header';
import SearchBar from "./components/Searchbar";
import {LogOut, Menu, User, X} from "lucide-react";
import {useLayout} from "../context/LayoutContext";


const Header: React.FC<HeaderProps> = ({
                                           className = '',
                                           logoText = 'Logo',
                                           showSearch = false,
                                           rightContent,
                                           onSearchChange
                                       }) => {

    return (
        <header className={`header ${className}`}>
            <div className="px-4 h-full flex items-center justify-between">
                {/* Left Side */}
                <div className="flex items-center">

                    <Link to="/dashboard" className="text-text text-xl font-bold ml-4">
                        {logoText}
                    </Link>
                </div>

                {/* Right Side */}
                <div className="flex items-center space-x-4">
                    {showSearch && <SearchBar onChange={onSearchChange}/>}
                    {rightContent}
                    <button
                        onClick={() => {
                            console.log('Logout clicked');
                        }}
                        className="text-text hover:bg-secondary p-2 rounded-md"
                    >
                        <LogOut className="h-6 w-6"/>
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Header;
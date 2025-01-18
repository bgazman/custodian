import React, {useState} from 'react';
import {useLayout} from './Layout';
import {Link, useLocation} from 'react-router-dom';
import ThemeSwitcher from './ThemeSwitcher';
import LayoutSwitcher from './LayoutSwitcher';
import {HeaderProps} from '../../types/header';
import SearchBar from "./Searchbar";
import {Bell, LogOut, Menu, User, X} from "lucide-react";


const Header = ({
                    logoText,
                    showSearch,
                    onSearchChange,
                    rightContent,
                    onCollapse,
                    position = 'right', // Default position for rightContent
                    className = '', // Add className prop
                }: HeaderProps) => {



    return (
        <>
            {/*/!* Overlay for Sidebar *!/*/}
            {/*{isOpen && (*/}
            {/*    <div*/}
            {/*        className="fixed inset-0 bg-black/50 z-40"*/}
            {/*        onClick={() => setIsOpen(false)}*/}
            {/*    />*/}
            {/*)}*/}


            {/* Navbar */}
            <header className={`fixed top-0 left-0 right-0 h-16 bg-primary shadow z-30 ${className}`}>
                <div className="px-4 h-full flex items-center justify-between">
                    {/* Left Side */}
                    <div className={`flex items-center ${
                        position === 'right'
                            ? 'order-last flex-row-reverse'
                            : ''
                    }`}>
                        <button
                            onClick={onCollapse}
                            className="text-text hover:bg-secondary p-2 rounded-md"
                        >
                            <Menu className="h-6 w-6"/>
                        </button>
                        <Link to="/dashboard" className={`text-text text-xl font-bold ${
                            position === 'right' ? 'mr-4' : 'ml-4'
                        }`}>
                            {logoText}
                        </Link>
                    </div>

                    {/* Right Side */}
                    <div className={`flex items-center space-x-4 ${
                        position === 'right'
                            ? 'order-first flex-row-reverse'
                            : ''
                    }`}>
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
        </>
    );
};

export default Header;
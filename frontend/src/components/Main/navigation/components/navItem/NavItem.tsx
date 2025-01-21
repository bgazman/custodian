import React from 'react';

export interface NavItemProps {
    icon: React.ComponentType; // Icon (function component)
    label: string; // Label for navigation item
    path: string; // Path/URL for navigation link
    className?: string; // Optional custom class
}

import { Link } from "react-router-dom";

export const NavItem: React.FC<NavItemProps> = ({ icon: Icon, label, path, className }) => {
    return (
        <Link to={path} className={`flex items-center space-x-2 ${className}`}>
            <Icon className="w-5 h-5" /> 
            <span>{label}</span>
        </Link>
    );
};
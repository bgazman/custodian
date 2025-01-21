
import React from 'react';
import './navigation.css'
import {NavItem, NavItemProps} from "./components/navItem/NavItem.tsx";



interface NavigationProps {
    items: NavItemProps[];
}

export const Navigation: React.FC<NavigationProps> = ({ items }) => {
    return (
        <nav className="navigation flex space-x-4 p-4 bg-gray-100">
            {items.map((item) => (
                <NavItem
                    key={item.path}
                    icon={item.icon}
                    label={item.label}
                    path={item.path}
                    className="text-sm text-gray-700 hover:underline"
                />
            ))}
        </nav>
    );
};
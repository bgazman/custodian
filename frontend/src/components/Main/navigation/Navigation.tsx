
import React from 'react';
import { NavigationProps } from './navigation.types';
import {Link} from "react-router-dom";
import {X} from "lucide-react";





const Navigation: React.FC<NavigationProps> = ({
                                                   items,
                                                   onToggle,
                                                   closeOnSelect = true,
                                               }) => {
    return (
        <aside className={`navigation`}>
            <div className="p-4 flex justify-between items-center border-b border-border">
                <h2 className="text-lg font-bold text-text">Menu</h2>
                <button
                    onClick={onToggle}
                    className="text-text hover:bg-secondary p-2 rounded-md"
                >
                    <X className="h-6 w-6"/>
                </button>
            </div>
            <nav className="p-4">
                {items.map((item) => (
                    <Link
                        key={item.path}
                        to={item.path}
                        className="flex items-center p-2 hover:bg-secondary rounded text-text mb-2"
                        onClick={() => closeOnSelect && onToggle()}
                    >
                        {item.icon && React.createElement(item.icon, {className: 'mr-2 h-5 w-5'})}
                        <span>{item.label}</span>
                    </Link>
                ))}
            </nav>
        </aside>
    );
};
export default Navigation;


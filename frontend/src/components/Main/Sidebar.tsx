import { Link } from 'react-router-dom';
import { SidebarProps } from '../../types/navigation'; // Ensure this path is correct
import React, {useState} from 'react';
import {X} from "lucide-react";

const Sidebar: React.FC<SidebarProps> = ({ items, isOpen, onToggle, position }) => {
    const translateClass = position === 'left'
        ? `${isOpen ? 'translate-x-0' : '-translate-x-full'}`
        : `${isOpen ? 'translate-x-0' : 'translate-x-full'}`;

    console.log('Position:', position);
    console.log('Transform class:', translateClass);

    return (
        <aside className={`
            fixed top-0 h-full w-64 
            bg-slate-800
            transform transition-transform duration-300
            z-50
            ${position === 'right' ? 'right-0' : 'left-0'}
            ${translateClass}
        `}>
            <div className="p-4 flex justify-between items-center">
                <h2 className="text-lg font-bold text-white">Menu</h2>
                <button onClick={onToggle}>
                    <X className="h-6 w-6 text-white"/>
                </button>
            </div>
            <nav className="p-4">
                {items.map((item) => (
                    <Link
                        key={item.path}
                        to={item.path}
                        className="flex items-center p-2 hover:bg-secondary rounded text-white mb-2"
                    >
                        {item.icon && React.createElement(item.icon, { className: 'mr-2 h-5 w-5' })}
                        <span>{item.label}</span>
                    </Link>
                ))}
            </nav>
        </aside>
    );
};
export default Sidebar;

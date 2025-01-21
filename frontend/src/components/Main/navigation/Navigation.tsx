
import React from 'react';
import { NavigationProps } from './navigation.types';
import {Link} from "react-router-dom";
import './navigation.css'




const Navigation: React.FC<NavigationProps> = ({
                                                   items,
                                                   onToggle,
                                                   closeOnSelect = true,
                                               }) => {
    return (

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

    );
};
export default Navigation;


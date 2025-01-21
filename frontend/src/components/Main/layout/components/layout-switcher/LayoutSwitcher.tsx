import React, {useEffect, useState} from 'react';
import { useLayout } from '../../../context/LayoutContext'; // Adjust the import path as needed
import { Layout as LayoutIcon } from 'lucide-react'; // Import an icon for the layout switcher

const layouts = [
    { id: 'top', label: 'Top Navigation', icon: <LayoutIcon /> },
    { id: 'aside-left', label: 'Aside Left', icon: <LayoutIcon /> },
    { id: 'aside-right', label: 'Aside Right', icon: <LayoutIcon /> },

];

const LayoutSwitcher = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { currentLayout, setLayout } = useLayout(); // Renamed for clarity

    const handleLayoutChange = (layoutId) => {
        console.log(layoutId)

        setLayout(layoutId);
        setIsOpen(false);
    };

    // Add click outside handler to close dropdown
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (isOpen && !event.target.closest('.layout-switcher')) {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [isOpen]);

    return (
        <div className="relative layout-switcher">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="text-text hover:bg-secondary p-2 rounded-md"
                aria-expanded={isOpen}
                aria-haspopup="true"
            >
                <LayoutIcon />
            </button>

            {isOpen && (
                <div
                    className="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-background border border-secondary"
                    role="menu"
                    aria-orientation="vertical"
                >
                    <div className="py-1">
                        {layouts.map((layoutOption) => (
                            <button
                                key={layoutOption.id}
                                onClick={() => handleLayoutChange(layoutOption.id)}
                                className="flex items-center w-full px-4 py-2 text-sm text-text hover:bg-secondary"
                                role="menuitem"
                                aria-current={currentLayout === layoutOption.id}
                            >
                                {layoutOption.icon && (
                                    <span className="mr-2">{layoutOption.icon}</span>
                                )}
                                {layoutOption.label}
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default LayoutSwitcher;

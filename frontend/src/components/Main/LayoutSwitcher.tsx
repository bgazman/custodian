import React, { useState } from 'react';
import { useLayout } from '../../context/LayoutContext'; // Adjust the import path as needed
import { Layout as LayoutIcon } from 'lucide-react'; // Import an icon for the layout switcher

const layouts = [
    { id: 'sidebar-left', label: 'Sidebar Left', icon: <LayoutIcon /> },
    { id: 'sidebar-right', label: 'Sidebar Right', icon: <LayoutIcon /> },
    { id: 'top-nav', label: 'Top Navigation', icon: <LayoutIcon /> },
    { id: 'centered', label: 'Centered', icon: <LayoutIcon /> },
    { id: 'split', label: 'Split View', icon: <LayoutIcon /> }
];

const LayoutSwitcher = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { layout, setLayout } = useLayout(); // Use the Layout context

    const handleLayoutChange = (layoutId) => {
        setLayout(layoutId); // Update the layout in context
        setIsOpen(false); // Close the dropdown
    };

    return (
        <div className="relative">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="text-text hover:bg-secondary p-2 rounded-md"
            >
                <LayoutIcon />
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-background border border-secondary">
                    <div className="py-1">
                        {layouts.map((layout) => (
                            <button
                                key={layout.id}
                                onClick={() => handleLayoutChange(layout.id)}
                                className="flex items-center w-full px-4 py-2 text-sm text-text hover:bg-secondary"
                            >
                                {layout.icon && <span className="mr-2">{layout.icon}</span>}
                                {layout.label}
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default LayoutSwitcher;

import {Palette} from "lucide-react";
import React, { useState } from 'react';
import { Theme } from '../../theme'; // Assuming Theme type is exported from ThemeContext

const themes: { name: Theme; label: string }[] = [
    {name: "default", label: "Default"},
    { name: 'minimal', label: 'Minimal' },
    { name: 'minimal-dark', label: 'Minimal Dark' },
    { name: 'amazon', label: 'Amazon' },
    { name: 'amazon-dark', label: 'Amazon Dark' },
];

const ThemeSwitcher: React.FC = () => {
    const [isOpen, setIsOpen] = useState(false);

    // Function to set the selected theme
    const setTheme = (theme: Theme) => {
        document.documentElement.setAttribute('data-theme', theme); // Update theme in DOM
        setIsOpen(false); // Close the dropdown
    };

    return (
        <div className="relative">
            {/* Toggle Button */}
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="text-text hover:bg-secondary p-2 rounded-md"
                aria-expanded={isOpen}
                aria-label="Theme Switcher"
            >
                <Palette /> {/* Replace with your icon */}
            </button>

            {/* Theme Dropdown */}
            {isOpen && (
                <div
                    className="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-background border border-secondary"
                    role="menu"
                    aria-orientation="vertical"
                    aria-labelledby="theme-switcher"
                >
                    <div className="py-1">
                        {themes.map((theme) => (
                            <button
                                key={theme.name}
                                onClick={() => setTheme(theme.name)}
                                className="flex items-center w-full px-4 py-2 text-sm text-text hover:bg-secondary"
                                role="menuitem"
                            >
                                {theme.label}
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default ThemeSwitcher;

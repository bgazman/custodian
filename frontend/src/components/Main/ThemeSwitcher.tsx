import { useState } from 'react';
import { Sun, Moon, Palette } from 'lucide-react';

const ThemeSwitcher = () => {
    const [isOpen, setIsOpen] = useState(false);
    const themes = [
        { name: 'minimal', label: 'Minimal', icon: <Sun /> },
        { name: 'minimal-dark', label: 'Minimal Dark', icon: <Moon /> },
        { name: 'elegant', label: 'Elegant Serif' },
        { name: 'elegant-dark', label: 'Elegant Serif Dark' },
        { name: 'vibrant', label: 'Vibrant' },
        { name: 'vibrant-dark', label: 'Vibrant Dark' },
        { name: 'nature', label: 'Nature' },
        { name: 'nature-dark', label: 'Nature Dark' }
    ];


    const setTheme = (theme) => {
        document.documentElement.setAttribute('data-theme', theme);
        setIsOpen(false);
    };

    return (
        <div className="relative">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="text-text hover:bg-secondary p-2 rounded-md"
            >
                <Palette />
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-background border border-secondary">
                    <div className="py-1">
                        {themes.map((theme) => (
                            <button
                                key={theme.name}
                                onClick={() => setTheme(theme.name)}
                                className="flex items-center w-full px-4 py-2 text-sm text-text hover:bg-secondary"
                            >
                                {theme.icon && <span className="mr-2">{theme.icon}</span>}
                                {theme.label || theme.name}
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default ThemeSwitcher;
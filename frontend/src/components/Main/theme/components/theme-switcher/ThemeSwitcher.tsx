import React from 'react';
import { Sun, Moon } from 'lucide-react';
import { useTheme } from '../../../context/ThemeContext';

interface ThemeSwitcherProps {
    className?: string;
}

const ThemeSwitcher: React.FC<ThemeSwitcherProps> = ({ className = '' }) => {
    const { theme, toggleTheme } = useTheme();
    const isDark = theme === 'minimal-dark';

    const handleClick = () => {
        toggleTheme();
    };

    return (
        <button
            onClick={handleClick}
            className={`
                rounded-md 
                hover:bg-hover
                p-2
                text-text
                bg-surface
                transition-colors 
                duration-200
                ${className}
            `}
            aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
        >
            {isDark ? (
                <Sun className="h-5 w-5" />
            ) : (
                <Moon className="h-5 w-5" />
            )}
        </button>
    );
};

export default ThemeSwitcher;
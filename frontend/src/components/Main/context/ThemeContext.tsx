// src/context/ThemeContext.tsx
import React, { createContext, useContext, useEffect, useState } from 'react';
import {Theme} from "../theme/theme";


interface ThemeContextType {
    theme: Theme;
    toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const useTheme = () => {
    const context = useContext(ThemeContext);
    if (!context) {
        throw new Error('useTheme must be used within a ThemeProvider');
    }
    return context;
};

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [theme, setTheme] = useState<Theme>(() => {
        // Try to get the saved theme
        if (typeof window !== 'undefined') {
            const savedTheme = localStorage.getItem('theme') as Theme;
            if (savedTheme) {
                return savedTheme;
            }
            // Check system preference
            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                return 'minimal-dark';
            }
        }
        return 'minimal';
    });

    useEffect(() => {
        // Save theme to localStorage
        localStorage.setItem('theme', theme);
        // Apply theme to document
        document.documentElement.setAttribute('data-theme', theme);
    }, [theme]);

    const toggleTheme = () => {
        console.log('Current theme:', theme); // Debug log
        setTheme(prevTheme => {
            const newTheme = prevTheme === 'minimal' ? 'minimal-dark' : 'minimal';
            console.log('Switching to:', newTheme); // Debug log
            return newTheme;
        });
    };

    return (
        <ThemeContext.Provider value={{ theme, toggleTheme }}>
            {children}
        </ThemeContext.Provider>
    );
};
import {createContext, useCallback, useContext, useEffect, useState} from "react";

const LayoutContext = createContext(undefined);

export const useLayout = () => {
    const context = useContext(LayoutContext);
    if (!context) throw new Error('useLayout must be used within LayoutProvider');
    return context;
};

export const LayoutProvider = ({ children }) => {
    // Define the available layout types
    type LayoutTypes = 'sidebar-left' | 'sidebar-right' | 'top';

    // Dynamically determine the initial layout
    const getInitialLayout = (): LayoutTypes => {
        // Example: Check local storage for a saved layout
        const savedLayout = localStorage.getItem('app-layout') as LayoutTypes;
        if (savedLayout) {
            return savedLayout; // Use saved layout if available
        }

        // Example: Determine layout based on screen size
        const isMobile = window.innerWidth <= 768;
        if (isMobile) {
            return 'top'; // Default to 'top' for smaller screens
        }

        // Default layout
        return 'sidebar-left';
    };

    // Use the function to initialize state
    const [layout, setLayout] = useState<LayoutTypes>(getInitialLayout());
    const [isOpen, setIsOpen] = useState(false);

    // Toggle logic for the layout
    const onToggle = useCallback(() => {
        setIsOpen(prev => !prev);
    }, []);

    // Update local storage whenever the layout changes
    useEffect(() => {
        localStorage.setItem('app-layout', layout);
    }, [layout]);

    return (
        <LayoutContext.Provider
            value={{
                layout,
                isOpen,
                setLayout,
                onToggle,
            }}
        >
            {children}
        </LayoutContext.Provider>
    );
};
import {createContext, useCallback, useContext, useState} from "react";

const LayoutContext = createContext(undefined);

export const useLayout = () => {
    const context = useContext(LayoutContext);
    if (!context) throw new Error('useLayout must be used within LayoutProvider');
    return context;
};

export const LayoutProvider = ({ children }) => {
    const [layout, setLayout] = useState<'sidebar-left' | 'sidebar-right' | 'top'>('sidebar-left'); // Restrict layout types
    const [isOpen, setIsOpen] = useState(false);

    const onToggle = useCallback(() => {
        setIsOpen(prev => !prev);
    }, []);

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
import React, { ReactNode } from 'react';
import { createContext, useContext, useState, useCallback } from 'react';

type LayoutType = 'sidebar-left' | 'sidebar-right' | 'top-nav' | 'centered' | 'split';

interface LayoutContextType {
    layout: LayoutType;
    collapsed: boolean;
    setLayout: (layout: LayoutType) => void;
    toggleCollapsed: () => void;
}

const LayoutContext = createContext<LayoutContextType | undefined>(undefined);

export const useLayout = () => {
    const context = useContext(LayoutContext);
    if (!context) throw new Error('useLayout must be used within LayoutProvider');
    return context;
};

interface LayoutProps {
    children: ReactNode;
}

export const Layout = ({ children }: LayoutProps) => {
    const [layout, setLayout] = useState<LayoutType>('sidebar-left');
    const [collapsed, setCollapsed] = useState(false);

    const toggleCollapsed = useCallback(() => {
        setCollapsed(prev => !prev);
    }, []);

    return (
        <LayoutContext.Provider value={{ layout, collapsed, setLayout, toggleCollapsed }}>
            <div className={`layout-container ${layout} ${collapsed ? 'collapsed' : ''}`}>
                {children}
            </div>
        </LayoutContext.Provider>
    );
};

export default Layout;

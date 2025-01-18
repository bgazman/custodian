// src/contexts/LayoutContext.tsx
import React, { createContext, useContext, useState, useEffect } from 'react';

type Layout = 'sidebar-left' | 'sidebar-right' | 'top-nav' | 'centered' | 'split';
type LayoutContextType = {
    layout: Layout;
    setLayout: (layout: Layout) => void;
};

const LayoutContext = createContext<LayoutContextType | undefined>(undefined);

export function LayoutProvider({ children }: { children: React.ReactNode }) {
    const [layout, setLayout] = useState<Layout>('sidebar-left'); // Default layout

    useEffect(() => {
        document.documentElement.dataset.layout = layout; // Update data-layout attribute
    }, [layout]);

    return (
        <LayoutContext.Provider value={{ layout, setLayout }}>
            {children}
        </LayoutContext.Provider>
    );
}

export function useLayout() {
    const context = useContext(LayoutContext);
    if (context === undefined) {
        throw new Error('useLayout must be used within a LayoutProvider');
    }
    return context;
}

import React, {ReactNode, useState} from 'react';
import { useLayout } from './Layout';

interface MainContentProps {
    children: ReactNode;
    isOpen: boolean;  // Add this
}
const MainContent = ({ children, isOpen }: MainContentProps) => {
    const { layout } = useLayout();

    const contentClass = {
        'centered': 'max-w-content mx-auto',
        'split': 'w-1/2',
        'sidebar-left': isOpen ? 'ml-64' : 'ml-0',
        'sidebar-right': isOpen ? 'mr-64' : 'mr-0'
    }[layout] || '';

    return (
        <main className={`min-h-screen ${contentClass}`} style={{ padding: 'var(--content-padding)', marginTop: 'var(--header-height)' }}>
            {children}
        </main>
    );
};

export default MainContent;
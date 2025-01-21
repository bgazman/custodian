import React, { useState } from 'react';
import {FloatingButton} from "../ui/floating-button/FloatingButton.tsx";
import LayoutSwitcher from "../layout/components/layout-switcher/LayoutSwitcher.tsx";
import ThemeSwitcher from "../theme/components/theme-switcher/ThemeSwitcher.tsx";
import {Settings} from "lucide-react";


import './devtools.css'; // Import the CSS file for styles

export interface DevToolsProps {
    initialIsOpen?: boolean;
    panelHeight?: number; // Drawer height
    panelWidth?: number; // Drawer width
}

export const DevTools = ({
                             initialIsOpen = false,
                             panelHeight = 500,
                             panelWidth = 500
                         }: DevToolsProps) => {
    const [isVisible, setIsVisible] = useState(initialIsOpen);

    return (
        <div className="devtools">
            {/* Floating Button */}
            <button
                className="floating-button"
                onClick={() => setIsVisible(!isVisible)}
            >
                <span className="sr-only">Toggle DevTools</span>
                <Settings />
            </button>

            {/* Centered Drawer */}
            <div
                className={`devtools-panel ${isVisible ? 'visible' : 'hidden'}`}
                style={{
                    width: panelWidth,
                    height: panelHeight
                }}
            >
                {/* Add any content here */}
                <h2 className="text-center">DevTools Panel</h2>

                <ThemeSwitcher/>
                <LayoutSwitcher/>
            </div>
        </div>
    );
};

export default DevTools;
import './devtools.css';

import React, { useState } from 'react';
import {FloatingButton} from "../ui/floating-button/FloatingButton.tsx";
import LayoutSwitcher from "../layout/components/layout-switcher/LayoutSwitcher.tsx";
import ThemeSwitcher from "../theme/components/theme-switcher/ThemeSwitcher.tsx";
import {Settings} from "lucide-react";



export interface DevToolsProps {
    initialIsOpen?: boolean;
    panelHeight?: number;
    panelWidth?: number;
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
            <FloatingButton
                className="floating-button"
                onClick={() => setIsVisible(!isVisible)}
            >
                <span className="sr-only">Toggle DevTools</span>
                <Settings />
            </FloatingButton>

            <div
                className={`devtools-panel ${isVisible ? 'visible' : 'hidden'}`}
                style={{
                    width: panelWidth,
                    height: panelHeight
                }}
            >
                <h2 className="text-center">DevTools Panel</h2>

                <ThemeSwitcher/>
                <LayoutSwitcher/>
            </div>
        </div>
    );
};

export default DevTools;
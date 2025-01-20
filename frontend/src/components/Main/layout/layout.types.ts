import React, {ReactNode} from "react";

export type IconComponent = React.ComponentType<{ className?: string, size?: number | string }>;


export type LayoutType = 'sidebar-left' | 'sidebar-right';

export interface LayoutProps {
    children: ReactNode;
    className?: string;
    layout: LayoutType;
}


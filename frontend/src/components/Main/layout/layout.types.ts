import React, {ReactNode} from "react";

export type IconComponent = React.ComponentType<{ className?: string, size?: number | string }>;


export type LayoutType = 'aside-left' | 'aside-right';

export interface LayoutProps {
    children: ReactNode;
    className?: string;
    layout: LayoutType;
}


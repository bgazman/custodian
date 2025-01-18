import {NavItem} from "./navigation";
import React from "react";

export interface HeaderProps {
    logoText?: string;
    showSearch?: boolean;
    onSearchChange?: (value: string) => void;
    rightContent?: React.ReactNode;
    collapsed?: boolean;
    onCollapse?: () => void;
    className?: string;
    position?: 'left' | 'right'; // New prop to indicate layout position
}

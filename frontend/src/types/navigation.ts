import React from "react";


export type NavItemType = 'link' | 'dropdown' | 'button';
export interface IconProps {
    className?: string;
    size?: number | string;
    [key: string]: any;
}

type IconComponent = React.ComponentType<{ className?: string, size?: number | string }>;


export interface NavItem {
    icon?: IconComponent | JSX.Element; // Optional icon as a React component or JSX element
    label: string;
    path: string;
    type?: NavItemType;
    children?: NavItem[];
    onClick?: () => void;
    disabled?: boolean;
    hidden?: boolean;
    permissions?: string[];
}





export interface SidebarProps {
    items: NavItem[]; // Array of navigation items
    mode?: 'static' | 'draggable' | 'collapsabl'; // // will implement later
    position?: 'left' | 'right'; // Sidebar position (left or right)
    collapsed?: boolean; // Whether the sidebar is collapsed
    onCollapse?: () => void; // Function to handle collapse/expand action
    closeOnSelect?: boolean; // Whether to close the sidebar when an item is selected
}

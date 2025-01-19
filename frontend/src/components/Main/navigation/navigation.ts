import {IconComponent, LayoutType} from "../layout/layout";

export interface NavigationProps {
    // Core props
    items: NavigationItem[];
    isOpen: boolean;

    // Callbacks
    onToggle: () => void;
    closeOnSelect?: boolean;

    // Styling
    className?: string;
}

export interface NavigationItem {
    icon?: IconComponent | JSX.Element; // Optional icon as a React component or JSX element
    label: string;
    path: string;
    type?: NavItemType;
    children?: NavigationItem[];
    onClick?: () => void;
    disabled?: boolean;
    hidden?: boolean;
    permissions?: string[];
}

export type NavItemType = 'link' | 'dropdown' | 'button';

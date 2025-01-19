import React from "react";

export interface HeaderProps {
    logoText?: string;
    showSearch?: boolean;
    onSearchChange?: (value: string) => void;
    rightContent?: React.ReactNode;
    onToggle: () => void;
    className?: string;
}
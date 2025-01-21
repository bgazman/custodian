import React from "react";

type SidebarProps = {
    children?: React.ReactNode; // Content inside the sidebar (e.g., Navigation)
};

const Sidebar: React.FC<SidebarProps> = ({ children }) => {
    return (
        <aside>
            {children}
        </aside>
    );
};

export default Sidebar;
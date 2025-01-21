import React from "react";

type SidebarProps = {
    children?: React.ReactNode; // Content inside the aside (e.g., Navigation)
};

const Aside: React.FC<SidebarProps> = ({ children }) => {
    return (
        <aside>
            {children}
        </aside>
    );
};

export default Aside;
interface LayoutProps {
    layout: string; // The layout type (e.g., "top", "sidebar-left")
    topbarSlot?: React.ReactNode; // Content for the topbar
    sidebarSlot?: React.ReactNode; // Content for the sidebar
    contentSlot?: React.ReactNode; // Content for the content content
}

const Layout: React.FC<LayoutProps> = ({
                                           layout, // "sidebar-left", "sidebar-right", "topbar-only", etc.
                                           topbarSlot,
                                           sidebarSlot,
                                           contentSlot,
                                       }) => {
    return (
        <div className={`layout`} data-layout={layout}>
            {topbarSlot && (
                <header className="topbar" style={{ gridArea: "topbar" }}>
                    {topbarSlot}
                </header>
            )}
            {sidebarSlot && (
                <aside className="sidebar" style={{ gridArea: "sidebar" }}>
                    {sidebarSlot}
                </aside>
            )}
            <main className="content" style={{ gridArea: "content" }}>
                {contentSlot}
            </main>
        </div>
    );
};
export default Layout;
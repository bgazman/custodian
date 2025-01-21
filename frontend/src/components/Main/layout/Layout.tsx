interface LayoutProps {
    layout: string; // The layout type (e.g., "top", "aside-left")
    headerSlot?: React.ReactNode; // Content for the header
    asideSlot?: React.ReactNode; // Content for the aside
    mainSlot?: React.ReactNode; // Content for the main main
}

const Layout: React.FC<LayoutProps> = ({
                                           layout, // "aside-left", "aside-right", "header-only", etc.
                                           headerSlot,
                                           asideSlot,
                                           mainSlot,
                                       }) => {
    return (
        <div className={`layout`} data-layout={layout}>
            {headerSlot && (
                <header className="header" style={{ gridArea: "header" }}>
                    {headerSlot}
                </header>
            )}
            {asideSlot && (
                <aside className="aside" style={{ gridArea: "aside" }}>
                    {asideSlot}
                </aside>
            )}
            <main className="main" style={{ gridArea: "main" }}>
                {mainSlot}
            </main>
        </div>
    );
};
export default Layout;
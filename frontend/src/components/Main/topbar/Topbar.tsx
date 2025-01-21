import './topbar.css'

const Topbar: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return (
        <header className="topbar">
            {children}
        </header>
    );
};

export default Topbar;
import './header.css'

const Header: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return (
        <div >
            {children}
        </div>
    );
};

export default Header;
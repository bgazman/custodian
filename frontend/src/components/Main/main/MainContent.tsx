import React from 'react';




const MainContent = ({ children, className = '' }) => {
    return (
        <main className={`main ${className}`}>
            <div className="p-4 h-full">
                {children}
            </div>
        </main>
    );
};

export default MainContent;
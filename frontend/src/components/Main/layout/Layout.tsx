import {ReactNode} from 'react';
import {useLayout} from "../context/LayoutContext";
import {LayoutProps} from './layout';

const Layout = ({children, className = '', data}: LayoutProps) => {
    const {layout} = useLayout();

    return (
        <div
            className={`data-layout ${className}`}
            data-layout={layout}
        >
            {children}
        </div>
    );
};

export default Layout;
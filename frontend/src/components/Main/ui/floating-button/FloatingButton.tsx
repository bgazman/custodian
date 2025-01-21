import React, { ButtonHTMLAttributes } from 'react';
import { Button } from '../button/Button.tsx';
import './floating-button.css';

interface FloatingButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'outline'
    size?: 'sm' | 'md' | 'lg'
    position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
}

export const FloatingButton = ({
                                   position = 'bottom-right',
                                   className,
                                   children,
                                   ...props
                               }: React.HTMLAttributes<HTMLButtonElement> & {
    position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
}) => {
    const positionClasses = {
        'top-left': 'top-4 left-4',
        'top-right': 'top-4 right-4',
        'bottom-left': 'bottom-4 left-4',
        'bottom-right': 'bottom-4 right-4',
    };

    return (
        <Button
            {...props}
            className={`
                   fixed ${positionClasses[position]} z-50
                   rounded-full shadow-lg transition-all
                   ${className || ''}
               `}
        >
            {children}
        </Button>
    );
};
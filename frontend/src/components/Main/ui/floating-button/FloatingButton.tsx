import React, { ButtonHTMLAttributes } from 'react';
import { Button } from '../button/Button.tsx';


interface FloatingButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'outline'
    size?: 'sm' | 'md' | 'lg'
    position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
}

export const FloatingButton = ({
                                   variant = 'primary',
                                   size = 'md',
                                   position = 'bottom-right',
                                   className,
                                   children,
                                   ...props
                               }: FloatingButtonProps) => {
    const positionClasses = {
        'top-left': 'top-[var(--space-lg)] left-[var(--space-lg)]',
        'top-right': 'top-[var(--space-lg)] right-[var(--space-lg)]',
        'bottom-left': 'bottom-[var(--space-lg)] left-[var(--space-lg)]',
        'bottom-right': 'bottom-[var(--space-lg)] right-[var(--space-lg)]'
    };

    return (
        <Button
            variant={variant}
            size={size}
            {...props}
            className={`
        fixed ${positionClasses[position]} z-[var(--z-popup)]
        rounded-full shadow-lg
        transition-all duration-[var(--transition-duration)]
        flex items-center justify-center
        ${className || ''}
      `}
        >
            {children}
        </Button>
    );
};
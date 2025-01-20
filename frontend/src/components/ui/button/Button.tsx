// components/ui/button.tsx
import { ButtonHTMLAttributes } from 'react'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'outline'
    size?: 'sm' | 'md' | 'lg'
}

export const Button = ({
                           variant = 'primary',
                           size = 'md',
                           children,
                           ...props
                       }: ButtonProps) => {
    return (
        <button
            {...props}
            className={`button
        ${variant === 'primary' ? 'bg-primary text-white hover:bg-primary-dark' : ''}
        ${variant === 'secondary' ? 'bg-secondary text-white hover:bg-secondary-dark' : ''}
        ${variant === 'outline' ? 'border-2 border-primary text-primary hover:bg-primary/10' : ''}
        ${size === 'sm' ? 'px-3 py-1.5 text-sm' : ''}
        ${size === 'md' ? 'px-4 py-2' : ''}
        ${size === 'lg' ? 'px-6 py-3 text-lg' : ''}
        ${props.disabled ? 'opacity-50 cursor-not-allowed' : ''}
        transition-all duration-200
      `}
        >
            {children}
        </button>
    )
}
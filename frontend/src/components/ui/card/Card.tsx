import { HTMLAttributes, ReactNode } from 'react'

interface CardProps extends HTMLAttributes<HTMLDivElement> {
    variant?: 'default' | 'bordered' | 'elevated'
    size?: 'sm' | 'md' | 'lg'
    children: ReactNode
}

export const Card = ({
                         variant = 'default',
                         size = 'md',
                         children,
                         ...props
                     }: CardProps) => {
    return (
        <div
            {...props}
            className={`card
        ${variant === 'default' ? 'bg-white' : ''}
        ${variant === 'bordered' ? 'bg-white border border-gray-200' : ''}
        ${variant === 'elevated' ? 'bg-white shadow-md' : ''}
        ${size === 'sm' ? 'p-3' : ''}
        ${size === 'md' ? 'p-4' : ''}
        ${size === 'lg' ? 'p-6' : ''}
        rounded-lg
        transition-all duration-200
      `}
        >
            {children}
        </div>
    )
}

export const CardHeader = ({ className = '', ...props }: HTMLAttributes<HTMLDivElement>) => (
    <div {...props} className={`mb-4 ${className}`} />
)

export const CardTitle = ({ className = '', ...props }: HTMLAttributes<HTMLHeadingElement>) => (
    <h3 {...props} className={`text-lg font-semibold text-gray-900 ${className}`} />
)

export const CardDescription = ({ className = '', ...props }: HTMLAttributes<HTMLParagraphElement>) => (
    <p {...props} className={`text-sm text-gray-500 ${className}`} />
)

export const CardContent = ({ className = '', ...props }: HTMLAttributes<HTMLDivElement>) => (
    <div {...props} className={`${className}`} />
)

export const CardFooter = ({ className = '', ...props }: HTMLAttributes<HTMLDivElement>) => (
    <div {...props} className={`mt-4 flex items-center ${className}`} />
)
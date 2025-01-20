import { InputHTMLAttributes } from 'react'

type BaseInputProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'>

interface InputProps extends BaseInputProps {
    variant?: 'primary' | 'secondary' | 'outline'
    size?: 'sm' | 'md' | 'lg'
}

export const Input = ({
                          variant = 'primary',
                          size = 'md',
                          ...props
                      }: InputProps) => {
    return (
        <input
            {...props}
            className={`input
        ${variant === 'primary' ? 'border-gray-300 focus:border-blue-500 focus:ring-blue-500' : ''}
        ${variant === 'secondary' ? 'border-gray-200 focus:border-gray-500 focus:ring-gray-500' : ''}
        ${variant === 'outline' ? 'border-2 focus:border-blue-500 focus:ring-blue-500' : ''}
        ${size === 'sm' ? 'px-3 py-1.5 text-sm' : ''}
        ${size === 'md' ? 'px-4 py-2' : ''} 
        ${size === 'lg' ? 'px-6 py-3 text-lg' : ''}
        ${props.disabled ? 'opacity-50 cursor-not-allowed bg-gray-100' : ''}
        w-full rounded-md border bg-white shadow-sm
        focus:outline-none focus:ring-2
        transition-all duration-200
      `}
        />
    )
}

export default Input
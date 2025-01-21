import './button.css'
import { ButtonHTMLAttributes } from 'react'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'outline'
    size?: 'sm' | 'md' | 'lg'
}

import React from 'react';

export const Button = ({
                           className = '',
                           ...props
                       }: React.ButtonHTMLAttributes<HTMLButtonElement>) => (
    <button className={`base-button ${className}`} {...props} />
);
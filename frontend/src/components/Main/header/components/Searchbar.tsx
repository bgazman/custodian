import React from 'react';
import { Search } from 'lucide-react';

interface SearchBarProps {
    onChange?: (value: string) => void;
    placeholder?: string;
}

const SearchBar = ({ onChange, placeholder = 'Search...' }: SearchBarProps) => {
    return (
        <div className="relative w-64">
            <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
                <Search className="h-4 w-4 text-text-muted" />
            </div>
            <input
                type="search"
                placeholder={placeholder}
                onChange={(e) => onChange?.(e.target.value)}
                className="w-full pl-10 pr-4 py-2 rounded-md bg-background
                    border border-border focus:border-primary/30
                    focus:ring focus:ring-primary/20 text-sm"
            />
        </div>
    );
};

export default SearchBar;
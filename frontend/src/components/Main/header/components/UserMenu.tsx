import { useState, useRef, useEffect } from 'react';
import { LogOut, Settings, User, ChevronDown } from 'lucide-react';

interface UserMenuProps {
    username?: string;
    email?: string;
    onLogout?: () => void;
    onProfileClick?: () => void;
    onSettingsClick?: () => void;
}

const UserMenu = ({
                      username = 'User',
                      email = 'user@example.com',
                      onLogout,
                      onProfileClick,
                      onSettingsClick
                  }: UserMenuProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
        <div className="relative" ref={menuRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center space-x-2 p-2 rounded-md hover:bg-secondary/10"
            >
                <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                    <User className="h-5 w-5 text-primary" />
                </div>
                {/*<ChevronDown className={`h-4 w-4 text-text-muted transition-transform ${isOpen ? 'rotate-180' : ''}`} />*/}
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-56 rounded-lg shadow-lg bg-background border border-border overflow-hidden">
                    <div className="p-3 border-b border-border">
                        <p className="text-sm font-medium text-text">{username}</p>
                        <p className="text-xs text-text-muted">{email}</p>
                    </div>
                    <nav className="py-2">
                        <button
                            onClick={() => {
                                onProfileClick?.();
                                setIsOpen(false);
                            }}
                            className="w-full flex items-center px-4 py-2 text-sm text-text-muted hover:bg-secondary/10"
                        >
                            <User className="mr-3 h-4 w-4" />
                            Your Profile
                        </button>
                        <button
                            onClick={() => {
                                onSettingsClick?.();
                                setIsOpen(false);
                            }}
                            className="w-full flex items-center px-4 py-2 text-sm text-text-muted hover:bg-secondary/10"
                        >
                            <Settings className="mr-3 h-4 w-4" />
                            Settings
                        </button>
                        <div className="my-2 border-t border-border"></div>
                        <button
                            onClick={() => {
                                onLogout?.();
                                setIsOpen(false);
                            }}
                            className="w-full flex items-center px-4 py-2 text-sm text-error hover:bg-error/10"
                        >
                            <LogOut className="mr-3 h-4 w-4" />
                            Sign out
                        </button>
                    </nav>
                </div>
            )}
        </div>
    );
};

export default UserMenu;
import {
    LayoutDashboard,
    ShieldCheck,
    Users,
    UserCog
} from 'lucide-react';
import {NavItem} from "../navigation/components/navItem/NavItem.tsx";

export const navigationItems: NavItem[] = [
    {
        icon: LayoutDashboard,
        label: 'Dashboard',
        path: '/dashboard'
    },

    {
        icon: UserCog,
        label: 'Admin',
        path: '/admin'
    }
];

export default navigationItems;
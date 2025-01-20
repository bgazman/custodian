import {
    LayoutDashboard,
    ShieldCheck,
    Users,
    UserCog
} from 'lucide-react';
import {NavigationItem} from "../navigation/navigation.types";

export const navigationItems: NavigationItem[] = [
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
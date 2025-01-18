import {
    LayoutDashboard,
    ShieldCheck,
    Users,
    UserCog
} from 'lucide-react';
import {NavItem} from "../types/navigation";

export const navigationItems: NavItem[] = [
    {
        icon: LayoutDashboard,
        label: 'Dashboard',
        path: '/dashboard'
    },
    {
        icon: ShieldCheck,
        label: 'IAM Dashboard',
        path: '/iam-dashboard'
    },
    {
        icon: UserCog,
        label: 'Admin',
        path: '/admin'
    },
    {
        icon: Users,
        label: 'User Details',
        path: '/users/:id'
    }
];

export default navigationItems;
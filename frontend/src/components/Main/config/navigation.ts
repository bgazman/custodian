import {
    LayoutDashboard,
    ShieldCheck,
    Users,
    UserCog
} from 'lucide-react';
import {NavigationItem} from "../navigation/navigation";

export const navigationItems: NavigationItem[] = [
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
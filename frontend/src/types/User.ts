import {UserRole} from "./UserRole";

export interface User {
    id: string;
    name: string;
    email: string;
    phoneNumber: string | null;
    enabled: boolean;
    accountNonLocked: boolean;
    lockedUntil: string | null;
    roleNames: string[];
    lastLoginTime: string;
    createdAt: Date;


}

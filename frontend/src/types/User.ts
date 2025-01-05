import {UserRole} from "./UserRole";

export interface User {
    id: string;
    name: string;
    email: string;
    enabled: boolean;
    createdAt: Date;
    lastLogin?: Date;
    permissions: string[];
    userRoles: UserRole[];
    status: "active" | "suspended" | "inactive";
    requiredMFA: boolean;

}


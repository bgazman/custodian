export interface User {
    id: string;
    name: string;
    email: string;
    enabled: boolean;
    createdAt: Date;
    lastLogin?: Date;
    permissions: string[];
    roles: string[];
    status: "active" | "suspended" | "inactive";
}


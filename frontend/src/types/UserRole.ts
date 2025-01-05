export interface UserRole {
    id: {
        userId: number;
        roleId: number;
    };
    role: {
        id: number;
        name: string;
        description: string;
        createdAt: string;
        updatedAt: string;
    };
    createdAt: string;
    updatedAt: string;
}
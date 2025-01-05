export interface Role {
    id: number;
    name: string;
    description: string | null;
    createdAt: string;  // Timestamp in ISO format
    updatedAt: string;  // Timestamp in ISO format
    parentRoleId: number | null;
}
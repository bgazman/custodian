export interface User {
    id: string; // Unique identifier
    name: string; // Full name
    email: string; // Email address
    roles: string[]; // Array of roles (e.g., 'admin', 'trader')
    permissions: string[]; // Array of specific permissions
    status: 'active' | 'suspended' | 'inactive'; // Account status
    createdAt: Date; // Timestamp of user creation
    lastLogin?: Date; // Timestamp of the last login (optional)
}

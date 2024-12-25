export interface GroupMembership {
    userId: number;
    groupId: number;
    role: string; // "ADMIN", "MEMBER"
}
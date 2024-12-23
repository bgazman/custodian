import { ApiClient } from "./common/ApiClient";
import { User } from "../types/User";

export const UserService = {
    /**
     * Fetch all users.
     * @returns A promise resolving to an array of users.
     */
    async fetchAllUsers(): Promise<User[]> {
        try {
            const users = await ApiClient.get<User[]>('/users');
            console.log('Fetched users:', users);
            return users;
        } catch (error: any) {
            console.error('Failed to fetch users:', error.message || error);
            throw error;
        }
    },

    /**
     * Fetch a single user by ID.
     * @param id - The ID of the user.
     * @returns A promise resolving to the user data.
     */
    async fetchUserById(id: number): Promise<User> {
        try {
            const user = await ApiClient.get<User>(`/users/${id}`);
            console.log('Fetched user:', user);
            return user;
        } catch (error: any) {
            console.error(`Failed to fetch user with ID ${id}:`, error.message || error);
            throw error;
        }
    },

    /**
     * Create a new user.
     * @param user - The user data to create.
     * @returns A promise resolving to the created user.
     */
    async createUser(user: User): Promise<User> {
        try {
            const createdUser = await ApiClient.post<User, User>('/users', user);
            console.log('User created:', createdUser);
            return createdUser;
        } catch (error: any) {
            console.error('Failed to create user:', error.message || error);
            throw error;
        }
    },

    /**
     * Update an existing user by ID.
     * @param id - The ID of the user.
     * @param user - The updated user data.
     * @returns A promise resolving to the updated user.
     */
    async updateUser(id: number, user: User): Promise<User> {
        try {
            const updatedUser = await ApiClient.put<User, User>(`/users/${id}`, user);
            console.log('User updated:', updatedUser);
            return updatedUser;
        } catch (error: any) {
            console.error(`Failed to update user with ID ${id}:`, error.message || error);
            throw error;
        }
    },

    /**
     * Delete a user by ID.
     * @param id - The ID of the user to delete.
     * @returns A promise resolving to void.
     */
    async deleteUser(id: number): Promise<void> {
        try {
            await ApiClient.delete(`/users/${id}`);
            console.log(`User with ID ${id} deleted.`);
        } catch (error: any) {
            console.error(`Failed to delete user with ID ${id}:`, error.message || error);
            throw error;
        }
    },

    /**
     * Enable a user by ID.
     * @param id - The ID of the user to enable.
     * @returns A promise resolving to void.
     */
    async enableUser(id: number): Promise<void> {
        try {
            await ApiClient.post(`/users/enable/${id}`, {});
            console.log(`User with ID ${id} enabled.`);
        } catch (error: any) {
            console.error(`Failed to enable user with ID ${id}:`, error.message || error);
            throw error;
        }
    },

    /**
     * Disable a user by ID.
     * @param id - The ID of the user to disable.
     * @returns A promise resolving to void.
     */
    async disableUser(id: number): Promise<void> {
        try {
            await ApiClient.post(`/users/disable/${id}`, {});
            console.log(`User with ID ${id} disabled.`);
        } catch (error: any) {
            console.error(`Failed to disable user with ID ${id}:`, error.message || error);
            throw error;
        }
    },
};
